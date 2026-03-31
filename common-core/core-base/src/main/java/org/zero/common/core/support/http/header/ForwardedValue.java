package org.zero.common.core.support.http.header;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.core.util.java.net.Endpoint;
import org.zero.common.core.util.java.net.InetAddressUtil;
import org.zero.common.data.constant.StringPool;

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
@EqualsAndHashCode
@RequiredArgsConstructor
public class ForwardedValue implements Iterable<ForwardedValue.Value> {
	public static final HttpHeader HEADER = HttpHeader.FORWARDED;

	protected static final String FOR = "for";
	protected static final String BY = "by";
	protected static final String HOST = "host";
	protected static final String PROTO = "proto";
	protected static final String UNKNOWN = "unknown";

	protected final Collection<Value> values;

	public static Builder builder() {
		return new Builder();
	}

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
		return values.stream().map(Value::toString).collect(Collectors.joining(StringPool.COMMA + StringPool.SPACE));
	}

	/**
	 * @author Zero (cnzeropro@163.com)
	 * @since 2025/12/23
	 */
	@Getter
	@EqualsAndHashCode
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
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
		 *
		 * @see HostValue
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
				String[] pair = StringUtil.splitToArray(part, StringPool.EQUAL, true);
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
			StringJoiner stringJoiner = new StringJoiner(StringPool.SEMICOLON + StringPool.SPACE);
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
		@EqualsAndHashCode
		@NoArgsConstructor(access = AccessLevel.PROTECTED)
		@AllArgsConstructor
		public static class IdentifierValue {
			/**
			 * IP 地址
			 */
			protected String ip;
			/**
			 * 端口号
			 */
			protected Integer port;
			/**
			 * 类型
			 *
			 * @see Type
			 */
			protected Type type;

			public IdentifierValue(String ip) {
				this(ip, null);
			}

			public IdentifierValue(String ip, Integer port) {
				this.ip = ip;
				this.port = port;
				this.type = this.detectType(ip);
			}

			protected Type detectType(String ip) {
				if (UNKNOWN.equalsIgnoreCase(ip)) {
					return Type.UNKNOWN;
				}
				if (StringUtil.startWith(ip, StringPool.UNDERSCORE)) {
					return Type.OBFUSCATED;
				}
				if (InetAddressUtil.isIPv4(ip)) {
					return Type.IPV4;
				}
				if (InetAddressUtil.isIPv6(ip)) {
					return Type.IPV6;
				}
				return Type.INVALID;
			}

			public static IdentifierValue parse(CharSequence text) {
				IdentifierValue result = new IdentifierValue();
				result.ip = Objects.toString(text, null);
				if (CharSequenceUtil.isEmpty(text)) {
					result.type = Type.INVALID;
					return result;
				}
				// 处理带引号的情况
				String value = text.toString().trim();
				if (value.startsWith(StringPool.DOUBLE_QUOTE) && value.endsWith(StringPool.DOUBLE_QUOTE)) {
					value = value.substring(1, value.length() - 1);
				}
				// unknown identifier
				if (UNKNOWN.equalsIgnoreCase(value)) {
					result.type = Type.UNKNOWN;
					return result;
				}
				// obfuscated identifier
				if (value.startsWith(StringPool.UNDERSCORE)) {
					result.type = Type.OBFUSCATED;
					return result;
				}
				Endpoint endpoint = Endpoint.parse(value);
				Endpoint.Type endpointType = endpoint.getType();
				if (endpointType == Endpoint.Type.IPV4 || endpointType == Endpoint.Type.IPV6) {
					result.type = endpointType == Endpoint.Type.IPV4 ? Type.IPV4 : Type.IPV6;
					result.ip = endpoint.getAddress();
					result.port = endpoint.getPort();
					return result;
				}
				result.type = Type.INVALID;
				return result;
			}

			@Override
			public String toString() {
				if (type == Type.INVALID) {
					return StringPool.EMPTY;
				}
				if (type == Type.UNKNOWN) {
					return UNKNOWN;
				}
				if (type == Type.OBFUSCATED) {
					return ip;
				}
				String portString = Objects.nonNull(port) ? StringPool.COLON + port : StringPool.EMPTY;
				if (type == Type.IPV6) {
					return StringPool.DOUBLE_QUOTE + StringPool.SQUARE_LEFT + ip + StringPool.SQUARE_RIGHT + StringPool.DOUBLE_QUOTE + portString;
				}
				return ip + portString;
			}

			public enum Type {
				/**
				 * IPv4，如：192.168.1.1、123.34.567.89
				 */
				IPV4,
				/**
				 * IPv6，如：::ffff:192.0.2.128、2001:db8:cafe::17
				 */
				IPV6,
				/**
				 * 混淆标识符，如：_hidden、_secret、_proxy1
				 */
				OBFUSCATED,
				/**
				 * 未知
				 */
				UNKNOWN,
				/**
				 * 无效
				 */
				INVALID;
			}
		}

		public static class Builder {
			protected IdentifierValue byValue;
			protected IdentifierValue forValue;
			protected HostValue hostValue;
			protected String protoValue;

			public Builder byValue(IdentifierValue byValue) {
				this.byValue = byValue;
				return this;
			}

			public Builder forValue(IdentifierValue forValue) {
				this.forValue = forValue;
				return this;
			}

			public Builder hostValue(HostValue hostValue) {
				this.hostValue = hostValue;
				return this;
			}

			public Builder protoValue(String protoValue) {
				this.protoValue = protoValue;
				return this;
			}

			public Value build() {
				return new Value(byValue, forValue, hostValue, protoValue);
			}
		}
	}

	public static class Builder {
		protected final Collection<Value> values = new ArrayList<>();

		public Builder values(Collection<Value> values) {
			this.values.addAll(values);
			return this;
		}

		public Builder value(Value value) {
			values.add(value);
			return this;
		}

		public ForwardedValue build() {
			return new ForwardedValue(values);
		}
	}
}
