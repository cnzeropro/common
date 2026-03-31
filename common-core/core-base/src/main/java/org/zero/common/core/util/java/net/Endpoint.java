package org.zero.common.core.util.java.net;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.data.constant.CharPool;
import org.zero.common.data.constant.StringPool;

import java.net.IDN;
import java.util.Objects;

import static org.zero.common.core.util.java.net.InetAddressUtil.isHostname;
import static org.zero.common.core.util.java.net.InetAddressUtil.isIPv4;
import static org.zero.common.core.util.java.net.InetAddressUtil.isIPv6;
import static org.zero.common.core.util.java.net.InetAddressUtil.isPort;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/1/4
 */
@Getter
@RequiredArgsConstructor
public class Endpoint {
	/**
	 * 原始地址
	 */
	private final String original;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 端口
	 */
	private Integer port;
	/**
	 * 类型
	 */
	private Type type;

	public static Endpoint parse(CharSequence text) {
		Endpoint endpoint = new Endpoint(Objects.toString(text, null));
		if (CharSequenceUtil.isBlank(text)) {
			return obtainType(endpoint);
		}
		String input = text.toString().trim();
		// 处理有方括号的情况，根据标准大概是 IPv6
		if (input.startsWith(StringPool.SQUARE_LEFT) && input.contains(StringPool.SQUARE_RIGHT)) {
			// 检查方括号后是否有冒号和端口
			int bracketEnd = input.indexOf(CharPool.SQUARE_RIGHT);
			endpoint.address = input.substring(1, bracketEnd);
			if (bracketEnd + 1 < input.length() && input.charAt(bracketEnd + 1) == CharPool.COLON) {
				String portString = input.substring(bracketEnd + 2);
				if (isValidPort(portString)) {
					endpoint.port = Integer.parseInt(portString);
				}
				return obtainType(endpoint);
			}
			// 只有方括号，没有端口
			if (bracketEnd + 1 == input.length()) {
				return obtainType(endpoint);
			}
		}
		// 处理普通情况（IPv4、主机名、不带方括号的 IPv6）
		int lastColonIndex = input.lastIndexOf(CharPool.COLON);
		// 没有冒号，将整个字符串作为地址
		if (lastColonIndex < 0) {
			endpoint.address = input;
			return obtainType(endpoint);
		}
		// 如果以冒号开头或结尾，不是有效的地址
		if (lastColonIndex == 0 || lastColonIndex == input.length() - 1) {
			return obtainType(endpoint);
		}
		String portCandidate = input.substring(lastColonIndex + 1);
		// 不是有效端口，有可能是 IPv6，将整个字符串作为地址尝试处理
		if (!isValidPort(portCandidate)) {
			endpoint.address = input;
			return obtainType(endpoint);
		}
		String addressCandidate = input.substring(0, lastColonIndex);
		// 检查地址部分是否包含多个冒号（可能是 IPv6）
		int colonCount = CharSequenceUtil.count(addressCandidate, CharPool.COLON);
		if (colonCount > 0) {
			// 可能是 IPv6，也可能是 IPv6 带端口但没有方括号（不符合标准但可能遇到）
			// 将整个字符串作为地址尝试处理，不符合标准不做处理（无奈之举，因为在没有方括号的情况下，IPv6 带端口和不带端口的区分基本没办法）
			endpoint.address = input;
			return obtainType(endpoint);
		}
		// IPv4或主机名带端口
		endpoint.address = addressCandidate;
		endpoint.port = Integer.parseInt(portCandidate);
		return obtainType(endpoint);
	}

	private static Endpoint obtainType(Endpoint endpoint) {
		if (CharSequenceUtil.isBlank(endpoint.address)) {
			endpoint.type = Type.INVALID;
			return endpoint;
		}
		if (isIPv4(endpoint.address)) {
			endpoint.type = Type.IPV4;
			return endpoint;
		}
		if (isIPv6(endpoint.address)) {
			endpoint.type = Type.IPV6;
			return endpoint;
		}
		if (isValidHostname(endpoint.address)) {
			endpoint.type = Type.HOSTNAME;
			return endpoint;
		}
		endpoint.type = Type.INVALID;
		return endpoint;
	}

	/**
	 * 检查是否是有效的端口
	 */
	private static boolean isValidPort(String text) {
		if (CharSequenceUtil.isBlank(text)) {
			return false;
		}
		if (!isPort(text)) {
			return false;
		}
		int port;
		try {
			port = Integer.parseInt(text);
		} catch (NumberFormatException ignored) {
			return false;
		}
		return port >= 0 && port <= 65535;
	}

	/**
	 * 检查是否是有效的主机名
	 */
	private static boolean isValidHostname(String hostname) {
		if (CharSequenceUtil.isBlank(hostname)) {
			return false;
		}
		// 检查转换后的主机名格式
		if (!isHostname(hostname, true)) {
			return false;
		}
		String asciiHostname;
		try {
			asciiHostname = IDN.toASCII(hostname);
		} catch (IllegalArgumentException ignored) {
			return false;
		}
		// 整个主机名（FQDN）的最大长度是255个字符，但实际使用中，域名的最大长度是253个字符（不包括最后的根标签的点）
		if (asciiHostname.length() > 253) {
			return false;
		}
		// 每个标签（由点分隔的部分）的长度为1到63个字符
		String[] labels = asciiHostname.split("\\.");
		for (String label : labels) {
			if (label.isEmpty() || label.length() > 63) {
				return false;
			}
			if (label.startsWith(StringPool.DASH) || label.endsWith(StringPool.DASH)) {
				return false;
			}
		}
		// 顶级域（TLD）不能全是数字
		String tld = labels[labels.length - 1];
		if (tld.matches("^\\d+$")) {
			return false;
		}
		// 标签之间不能有空标签（连续的点..）
		return !hostname.contains(StringPool.DOUBLE_DOT);
	}

	public enum Type {
		IPV4,
		IPV6,
		HOSTNAME,
		INVALID
	}
}
