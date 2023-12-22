package org.zero.common.core.util.javax.net;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

/**
 * @author yufa.wang (yufa.wang@ronganchina.com)
 * @since 2022/6/17
 */
@UtilityClass
public class IpUtil {
    public final String LOCAL_IPV4 = "127.0.0.1";
    public final String LOCAL_IPV6 = "0:0:0:0:0:0:0:1";
    public final String[] IP_HEADERS = {
            // XFF最早由Squid缓存代理服务器引入使用，如今它已经成为标准，被各大HTTP代理、负载均衡等转发服务广泛使用，并被写入RFC 7239（Forwarded HTTP Extension）标准之中
            "X-Forwarded-For",
            // nginx
            "X-Real-IP",
            // httpd
            "Proxy-Client-IP",
            // weblogic
            "WL-Proxy-Client-IP",
            // 其他一些代理服务器
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR",
    };

    private final String UNKNOWN = "unknown";
    private final String IPS_DELIMITER = ",";
    private final String IPV4_DELIMITER = ".";
    private final String IPV6_DELIMITER = ":";
    private final String IPV6_IDENTIFIER_DELIMITER = "%";

    /**
     * 获取远程IP地址
     *
     * @param request Http Servlet 请求
     * @return IP地址
     */
    public String getRemoteIp(HttpServletRequest request) {
        String ip = null;
        for (String subIp : getRemoteIps(request)) {
            if (isNotUnknown(subIp)) {
                ip = subIp;
                break;
            }
        }
        return ip;
    }

    /**
     * 获取远程IP地址数组
     *
     * @param request Http Servlet 请求
     * @return IP地址数组
     */
    public String[] getRemoteIps(HttpServletRequest request) {
        String[] ips = new String[0];
        String ipsStr = getRemoteIpsStr(request);
        if (StringUtils.hasText(ipsStr)) {
            ips = ipsStr.trim().split(IPS_DELIMITER);
        }
        return ips;
    }

    /**
     * 获取远程IP地址，如果使用代理，可能存在多个
     *
     * @param request Http Servlet 请求
     * @return IP地址，可能有多个，以“,”分隔
     */
    public String getRemoteIpsStr(HttpServletRequest request) {
        String ipsStr = null;
        for (String header : IP_HEADERS) {
            String currentIpsStr = request.getHeader(header);
            if (isNotUnknown(currentIpsStr)) {
                ipsStr = currentIpsStr;
                break;
            }
        }

        // 如果指定请求头一个都没有，使用HttpServletRequest.getRemoteAddr方法获取
        if (Objects.isNull(ipsStr)) {
            ipsStr = request.getRemoteAddr();
        }
        return ipsStr;
    }

    /**
     * 获取本机IPv4地址
     *
     * @return 本机IPv4地址
     */
    @SneakyThrows
    public String getLocalIpv4() {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * 获取本机全部IPv4地址
     *
     * @return 本机IPv4地址数组
     */
    public String[] getLocalIpv4s() {
        String[] localIps = getLocalIps();
        return Arrays.stream(localIps).filter(ip -> !ip.contains(IPV6_DELIMITER)).toArray(String[]::new);
    }

    /**
     * 获取本机全部IPv6地址
     *
     * @return 本机IPv6地址数组
     */
    public String[] getLocalIpv6s() {
        String[] localIps = getLocalIps();
        return Arrays.stream(localIps).filter(ip -> ip.contains(IPV6_DELIMITER)).toArray(String[]::new);
    }

    /**
     * 获取本机全部IP地址（包括IPv4和IPv6）
     *
     * @return 本机IP地址数组
     */
    public String[] getLocalIps() {
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
     * 获取本机全部IP地址（包括IPv4和IPv6），但IPv6会携带一些额外信息，如网络接口名称等等
     * 另外该方法不会统计环回地址，没有启用的网卡地址，虚拟网卡地址和点对点网络接口地址
     *
     * @return 本机IP地址数组
     */
    @SneakyThrows
    public String[] getLocalIpsWithInfo() {
        List<String> ips = new ArrayList<>();
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            if (networkInterface.isUp() && !networkInterface.isLoopback() && !networkInterface.isVirtual() && !networkInterface.isPointToPoint()) {
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    ips.add(inetAddress.getHostAddress());
                }
            }
        }
        return ips.toArray(new String[0]);
    }

    public boolean isIpv4(String ipv4Str) {
        String ipFirstRegex = "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|[1-9])";
        String ipOtherRegex = "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d|\\d)";
        String ipDotRegex = "\\" + IPV4_DELIMITER;
        String regex = ipFirstRegex + ipDotRegex + ipOtherRegex + ipDotRegex + ipOtherRegex + ipDotRegex + ipOtherRegex;
        return ipv4Str.matches(regex);
    }

    private boolean isNotUnknown(String ip) {
        return !UNKNOWN.equalsIgnoreCase(ip);
    }
}
