package org.zero.common.core.util.java.net;

import lombok.SneakyThrows;
import org.zero.common.core.extension.java.util.function.ThrowableSupplier;
import org.zero.common.core.extension.java.util.function.ToBoolFunction;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.ThrowableUtil;
import org.zero.common.core.util.java.util.stream.StreamUtil;
import org.zero.common.data.constant.StringPool;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Enumeration;
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

    protected static final String IPV4_OCTET_REGEX = "(25[0-5]|2[0-4]\\d|1\\d{2}|0|[1-9]\\d|[1-9])";
    protected static final String IPV4_REGEX = "^" + IPV4_OCTET_REGEX + "(\\." + IPV4_OCTET_REGEX + "){3}$";
    protected static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

    public static boolean isIpv4(String str) {
        return CharSequenceUtil.mapIfNonBlank(str, (ToBoolFunction<String>) s -> IPV4_PATTERN.matcher(s).matches());
    }

    protected InetAddressUtil() {
        throw new UnsupportedOperationException();
    }
}
