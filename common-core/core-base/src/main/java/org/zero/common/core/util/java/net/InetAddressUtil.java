package org.zero.common.core.util.java.net;

import lombok.SneakyThrows;
import org.zero.common.core.extension.java.util.function.ThrowableSupplier;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.ThrowableUtil;
import org.zero.common.core.util.java.util.stream.StreamUtil;
import org.zero.common.data.constant.StringPool;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author zero
 * @since 2022/6/17
 */
public class InetAddressUtil {
	public static final String LOCAL_IPV4 = "127.0.0.1";
	public static final String LOCAL_IPV6 = "0:0:0:0:0:0:0:1";
	public static final String IPS_DELIMITER = StringPool.COMMA;
	public static final String IPV4_DELIMITER = StringPool.DOT;
	public static final String IPV6_DELIMITER = StringPool.COLON;
	public static final String IPV6_IDENTIFIER_DELIMITER = StringPool.PERCENT;

	/**
	 * 获取本机IPv4地址
	 *
	 * @return 本机IPv4地址
	 */
	@SneakyThrows
	public static String getLocalIpv4() {
		return InetAddress.getLocalHost().getHostAddress();
	}

	/**
	 * 获取本机全部IPv4地址
	 *
	 * @return 本机IPv4地址数组
	 */
	public static String[] getLocalIpv4s() {
		String[] localIps = getLocalIps();
		return Arrays.stream(localIps).filter(ip -> !ip.contains(IPV6_DELIMITER)).toArray(String[]::new);
	}

	/**
	 * 获取本机全部IPv6地址
	 *
	 * @return 本机IPv6地址数组
	 */
	public static String[] getLocalIpv6s() {
		String[] localIps = getLocalIps();
		return Arrays.stream(localIps).filter(ip -> ip.contains(IPV6_DELIMITER)).toArray(String[]::new);
	}

	/**
	 * 获取本机全部IP地址（包括IPv4和IPv6）
	 *
	 * @return 本机IP地址数组
	 */
	public static String[] getLocalIps() {
		String[] localIps = getLocalIpsWithInfo();
		return Arrays.stream(localIps).map(ip -> {
			// IPv6地址会携带一些额外信息，进行截断处理
			if (ip.contains(IPV6_IDENTIFIER_DELIMITER)) {
				return ip.substring(0, ip.indexOf(IPV6_IDENTIFIER_DELIMITER));
			}
			return ip;
		}).toArray(String[]::new);
	}

	/**
	 * 获取本机全部 IP 地址（包括 IPv4 和 IPv6）
	 * <p>
	 * 另外该方法不会统计环回地址，没有启用的网卡地址，虚拟网卡地址和点对点网络接口地址
	 * <p>
	 * 注意：IPv6 地址会携带一些额外信息，如网络接口名称等等
	 *
	 * @return 本机 IP 地址数组
	 */
	public static String[] getLocalIpsWithInfo() {
		return ThrowableUtil.ignoreOpt((ThrowableSupplier<Enumeration<NetworkInterface>>) NetworkInterface::getNetworkInterfaces)
			.map(StreamUtil::of)
			.orElseGet(Stream::empty)
			.filter(networkInterface -> ThrowableUtil.ignore(networkInterface, NetworkInterface::isUp) &&
				!ThrowableUtil.ignore(networkInterface, NetworkInterface::isLoopback) &&
				!ThrowableUtil.ignore(networkInterface, NetworkInterface::isVirtual) &&
				!ThrowableUtil.ignore(networkInterface, NetworkInterface::isPointToPoint))
			.map(NetworkInterface::getInetAddresses)
			.flatMap(StreamUtil::of)
			.map(InetAddress::getHostAddress)
			.toArray(String[]::new);
	}

	/**
	 * IPv4 正则表达式（不包含端口）
	 */
	public static final String IPV4_REGEX = "^(?:(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\.){3}" +
		"(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])$";
	/**
	 * IPv4 正则表达式（不包含端口）
	 */
	public static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

	/**
	 * IPv6 子式
	 */
	public static final String IPV6_HEX_SEGMENT = "[0-9a-fA-F]{1,4}";

	/**
	 * 标准 IPv6 地址（8个段）
	 */
	public static final String IPV6_FULL = "(?:" + IPV6_HEX_SEGMENT + ":){7}" + IPV6_HEX_SEGMENT;

	/**
	 * 压缩的 IPv6 地址（包含::）
	 */
	public static final String IPV6_COMPRESSED_PREFIX = "(?:(?:" + IPV6_HEX_SEGMENT + ":){0,6}" + IPV6_HEX_SEGMENT + ")?::" +
		"(?:(?:" + IPV6_HEX_SEGMENT + ":){0,6}" + IPV6_HEX_SEGMENT + ")?";

	/**
	 * IPv4 映射的 IPv6 地址
	 */
	public static final String IPV4_PART = "(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])";
	public static final String IPV4_IN_IPV6 = "(?:" + IPV4_PART + "\\.){3}" + IPV4_PART;

	/**
	 * IPv4 兼容的 IPv6 地址
	 */
	public static final String IPV6_IPV4_COMPAT = "::(?:ffff:0:0:0:0:)?" + IPV4_IN_IPV6;

	/**
	 * 混合格式（IPv6 最后 4 个字节用 IPv4 表示）
	 */
	public static final String IPV6_MIXED = "(?:" + IPV6_HEX_SEGMENT + ":){6}" + IPV4_IN_IPV6;

	/**
	 * 压缩的混合格式
	 */
	public static final String IPV6_COMPRESSED_MIXED = "(?:(?:" + IPV6_HEX_SEGMENT + ":){0,4}" + IPV6_HEX_SEGMENT + ")?::" +
		"(?:(?:" + IPV6_HEX_SEGMENT + ":){0,4})?" + IPV4_IN_IPV6;

	/**
	 * 完整的 IPv6 正则表达式（不包含端口）
	 */
	public static final String IPV6_REGEX = String.format(
		"^\\[?(?:%s|%s|%s|%s|%s)\\]?$",
		IPV6_FULL,
		IPV6_COMPRESSED_PREFIX,
		IPV6_IPV4_COMPAT,
		IPV6_MIXED,
		IPV6_COMPRESSED_MIXED
	);
	// public static final String IPV6_REGEX = "^\\[?(" +
	// 	// 标准IPv6格式
	// 	"([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|" +
	// 	// 压缩的IPv6格式（::只能出现一次）
	// 	"(([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4})?::([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4}|" +
	// 	// IPv4映射的IPv6地址
	// 	"::[fF]{4}:((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\.){3}" +
	// 	"(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])|" +
	// 	// 混合格式（IPv6+IPv4）
	// 	"([0-9a-fA-F]{1,4}:){6}:?((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\.){3}" +
	// 	"(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])" +
	// 	")]?$";

	/**
	 * IPv6 正则表达式（不包含端口）
	 */
	public static final Pattern IPV6_PATTERN = Pattern.compile(IPV6_REGEX, Pattern.CASE_INSENSITIVE);

	public static final String HOSTNAME_LABEL = "(?:(?:[a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9]))";

	/**
	 * 主机名正则表达式（不包含端口）
	 */
	public static final String HOSTNAME_REGEX = "^(?:" + HOSTNAME_LABEL + "\\.)*" + HOSTNAME_LABEL + "$";

	/**
	 * 主机名正则表达式（不包含端口）
	 */
	public static final Pattern HOSTNAME_PATTERN = Pattern.compile(HOSTNAME_REGEX);

	/**
	 * 支持国际化域名（IDN）的正则表达式（不包含端口）
	 */
	public static final String HOSTNAME_IDN_REGEX =
		"^(?:(?:[a-zA-Z0-9\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]" +
			"|[a-zA-Z0-9\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]" +
			"[a-zA-Z0-9\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\-]*" +
			"[a-zA-Z0-9\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])\\.)*" +
			"(?:[a-zA-Z0-9\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]" +
			"|[a-zA-Z0-9\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]" +
			"[a-zA-Z0-9\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF\\-]*" +
			"[a-zA-Z0-9\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])$";
	/**
	 * 支持国际化域名（IDN）的正则表达式（不包含端口）
	 */
	public static final Pattern HOSTNAME_IDN_PATTERN = Pattern.compile(HOSTNAME_IDN_REGEX);

	/**
	 * 端口号正则表达式（0-65535）
	 */
	public static final String PORT_REGEX = "^(?:0|[1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$";
	/**
	 * 端口号正则表达式（0-65535）
	 */
	public static final Pattern PORT_PATTERN = Pattern.compile(PORT_REGEX);

	public static boolean isIPv4(String address) {
		if (CharSequenceUtil.isBlank(address)) {
			return false;
		}
		return IPV4_PATTERN.matcher(address).matches();
	}

	public static boolean isIPv6(String address) {
		if (CharSequenceUtil.isBlank(address)) {
			return false;
		}
		return IPV6_PATTERN.matcher(address).matches();
	}

	public static boolean isHostname(String address) {
		return isHostname(address, true);
	}

	public static boolean isHostname(String address, boolean allowIdn) {
		if (CharSequenceUtil.isBlank(address)) {
			return false;
		}
		Matcher matcher = allowIdn ? HOSTNAME_IDN_PATTERN.matcher(address) : HOSTNAME_PATTERN.matcher(address);
		return matcher.matches();
	}

	public static boolean isPort(String port) {
		if (CharSequenceUtil.isBlank(port)) {
			return false;
		}
		return PORT_PATTERN.matcher(port).matches();
	}

	protected InetAddressUtil() {
		throw new UnsupportedOperationException();
	}
}
