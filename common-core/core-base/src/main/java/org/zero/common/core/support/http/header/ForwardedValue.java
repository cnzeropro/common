package org.zero.common.core.support.http.header;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.data.constant.StringPool;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Reference/Headers/Forwarded">Forwarded</a>
 * @since 2025/12/23
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ForwardedValue implements Iterable<ForwardedValue.Value> {
	public static final String HEADER_NAME = "Forwarded";
	public static final String FOR = "for";
	public static final String BY = "by";
	public static final String HOST = "host";
	public static final String PROTO = "proto";
	public static final String UNKNOWN = "unknown";

	protected Collection<Value> values;

	public static ForwardedValue parse(CharSequence text) {
		if (CharSequenceUtil.isEmpty(text)) {
			throw new IllegalArgumentException("text is empty");
		}
		Collection<String> parts = StringUtil.split(text.toString(), StringPool.COMMA, true);
		Collection<Value> values = new ArrayList<>(parts.size());
		for (String part : parts) {
			Value value = Value.parse(part);
			values.add(value);
		}
		return new ForwardedValue(values);
	}

	@Override
	public Iterator<Value> iterator() {
		return values.iterator();
	}

	@Override
	public String toString() {
		return values.stream().map(Value::toString).collect(Collectors.joining(StringPool.COMMA));
	}

	/**
	 * @author Zero (cnzeropro@163.com)
	 * @since 2025/12/23
	 */
	@Getter
	public static class Value {
		/**
		 * 该请求进入到代理服务器的接口
		 */
		protected IdentifierValue byValue;
		/**
		 * 发起请求的客户端以及代理链中的一系列的代理服务器
		 */
		protected IdentifierValue forValue;
		/**
		 * 代理接收到的 <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Reference/Headers/Host">Host</a> 首部的信息
		 */
		protected HostValue hostValue;
		/**
		 * 协议（通常是 "http" 或者 "https"）
		 */
		protected String protoValue;

		public static Value parse(CharSequence text) {
			if (CharSequenceUtil.isEmpty(text)) {
				throw new IllegalArgumentException("text is empty");
			}
			Collection<String> parts = StringUtil.split(text.toString(), StringPool.SEMICOLON, true);
			Value result = new Value();
			for (String part : parts) {
				String[] pair = StringUtil.split(part, StringPool.EQUAL, true).toArray(new String[0]);
				if (pair.length != 2) {
					throw new IllegalArgumentException(part + " is invalid");
				}
				String name = pair[0];
				String value = pair[1];
				if (BY.equalsIgnoreCase(name)) {
					result.byValue = IdentifierValue.parse(value);
				} else if (FOR.equalsIgnoreCase(name)) {
					result.forValue = IdentifierValue.parse(value);
				} else if (HOST.equalsIgnoreCase(name)) {
					result.hostValue = HostValue.parse(value);
				} else if (PROTO.equalsIgnoreCase(name)) {
					result.protoValue = value;
				} else {
					throw new IllegalArgumentException(name + " is invalid");
				}
			}
			return result;
		}

		@Override
		public String toString() {
			StringJoiner stringJoiner = new StringJoiner(StringPool.SEMICOLON);
			if (Objects.nonNull(byValue)) {
				stringJoiner.add(BY + StringPool.EQUAL + byValue);
			}
			if (Objects.nonNull(forValue)) {
				stringJoiner.add(FOR + StringPool.EQUAL + forValue);
			}
			if (Objects.nonNull(hostValue)) {
				stringJoiner.add(HOST + StringPool.EQUAL + hostValue);
			}
			if (Objects.nonNull(protoValue)) {
				stringJoiner.add(PROTO + StringPool.EQUAL + protoValue);
			}
			return stringJoiner.toString();
		}

		@Getter
		public static class IdentifierValue {
			/**
			 * IP 地址或主机名
			 */
			protected String address;
			/**
			 * 端口号
			 */
			protected Integer port;
			/**
			 * 类型
			 *
			 * @see IdentifierType
			 */
			protected IdentifierType type;

			public static IdentifierValue parse(CharSequence text) {
				IdentifierValue result = new IdentifierValue();
				result.address = Objects.toString(text, null);
				if (CharSequenceUtil.isEmpty(text)) {
					result.type = IdentifierType.UNKNOWN;
					return result;
				}
				// 处理带引号的情况
				String value = text.toString();
				if (value.startsWith(StringPool.DOUBLE_QUOTE) && value.endsWith(StringPool.DOUBLE_QUOTE)) {
					value = value.substring(1, value.length() - 1);
				}
				if (UNKNOWN.equalsIgnoreCase(value)) {
					result.type = IdentifierType.UNKNOWN;
					return result;
				}
				// 可能是 obfuscated identifier，如 "_hidden" 或 "_secret"
				if (value.startsWith(StringPool.UNDERLINE)) {
					result.type = IdentifierType.OBFUSCATED;
					return result;
				}
				result.address = value;
				// 分离 IP 和端口（如果存在）
				if (value.contains(StringPool.COLON)) {
					// 检查是否是 IPv6 地址（带方括号）
					if (value.startsWith(StringPool.SQUARE_LEFT) && value.contains(StringPool.SQUARE_RIGHT)) {
						// IPv6 地址可能包含端口，如 [2001:db8::1]:8080
						int endBracketIndex = value.indexOf(StringPool.SQUARE_RIGHT);
						result.address = value.substring(1, endBracketIndex);
						if (endBracketIndex < value.length() - 1) {
							String portPart = value.substring(endBracketIndex + 2);
							result.port = Integer.parseInt(portPart);
						}
					} else {
						// IPv4 地址带端口，如 192.168.1.1:8080
						int lastColonIndex = value.lastIndexOf(StringPool.COLON);
						result.address = value.substring(0, lastColonIndex);
						if (lastColonIndex < value.length() - 1) {
							String portPart = value.substring(lastColonIndex + 1);
							result.port = Integer.parseInt(portPart);
						}
					}
				}
				try {
					// 尝试解析为 InetAddress
					InetAddress inetAddress = InetAddress.getByName(result.address);
					if (inetAddress instanceof Inet4Address) {
						result.type = IdentifierType.IPV4;
						return result;
					} else if (inetAddress instanceof Inet6Address) {
						result.type = IdentifierType.IPV6;
						return result;
					}
				} catch (UnknownHostException ignored) {
				}

				result.type = IdentifierType.HOSTNAME;
				return result;
			}

			@Override
			public String toString() {
				if (type == IdentifierType.UNKNOWN) {
					return UNKNOWN;
				}
				if (type == IdentifierType.OBFUSCATED) {
					return address;
				}
				String result = address + (Objects.nonNull(port) ? StringPool.COLON + port : StringPool.EMPTY);
				if (type == IdentifierType.IPV6) {
					return StringPool.DOUBLE_QUOTE + StringPool.SQUARE_LEFT + result + StringPool.SQUARE_RIGHT + StringPool.DOUBLE_QUOTE;
				}
				return result;
			}
		}

		public enum IdentifierType {
			/**
			 * IPv4地址，如 192.168.1.1
			 */
			IPV4,
			/**
			 * IPv6地址，如 2001:db8::1
			 */
			IPV6,
			/**
			 * 主机名，如 example.com
			 */
			HOSTNAME,
			/**
			 * 混淆标识符，如 _hidden
			 */
			OBFUSCATED,
			/**
			 * 未知类型
			 */
			UNKNOWN,
			;
		}
	}
}
