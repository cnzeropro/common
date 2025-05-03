package org.zero.common.core.util.javax.servlet;

import lombok.SneakyThrows;
import org.zero.common.core.util.java.io.IoUtil;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.data.constant.StringPool;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/3
 */
public class RequestUtil {
    public static final String[] IP_HEADERS = {
            // XFF 最早由 Squid 缓存代理服务器引入使用，如今它已经成为标准，被各大 HTTP 代理、负载均衡等转发服务广泛使用，并被写入 RFC 7239（Forwarded HTTP Extension）标准之中
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
    protected static final String UNKNOWN = "unknown";

    public String getIp(HttpServletRequest request) {
        return getIps(request).stream()
                .findFirst()
                .orElse(UNKNOWN);
    }

    public List<String> getIps(HttpServletRequest request) {
        List<String> ips = getIpMap(request).values()
                .stream()
                .flatMap(Collection::stream)
                .filter(ip -> CharSequenceUtil.nonBlank(ip) && !UNKNOWN.equalsIgnoreCase(ip))
                .distinct()
                .collect(Collectors.toList());
        ips.add(request.getRemoteAddr());
        return ips;
    }

    public Map<String, List<String>> getIpMap(HttpServletRequest request) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (String header : IP_HEADERS) {
            String ipsStr = request.getHeader(header);
            List<String> ips = StringUtil.split(ipsStr, StringPool.COMMA);
            result.put(header, ips);
        }
        return result;
    }

    /**
     * 简化
     *
     * @param parameterMap 参数 map
     * @param needSplit    是否需要分割，当参数值为逗号间隔的列表时，需要分割
     * @return 简化后的参数 map
     */
    public static Map<String, Object> simplify(Map<String, String[]> parameterMap, boolean needSplit) {
        Map<String, Object> result = new LinkedHashMap<>();
        parameterMap.forEach((key, values) -> {
            if (Objects.isNull(values)) {
                result.put(key, null);
                return;
            }
            String[] processedValues = needSplit ?
                    Arrays.stream(values)
                            .map(s -> StringUtil.split(s, StringPool.COMMA))
                            .flatMap(Collection::stream)
                            .toArray(String[]::new) :
                    Arrays.copyOf(values, values.length);
            Object finalValue = processedValues.length == 1 ? processedValues[0] : processedValues;
            result.put(key, finalValue);
        });
        return result;
    }

    /**
     * protocol://host[:port]/path?query#fragment
     * <p>
     * 示例：http://127.0.0.1:8080/demo/test?a=bbb <br>
     * 结果：http://127.0.0.1:8080/demo
     */
    public static String getDomain(HttpServletRequest request) {
        return String.format("%s://%s:%d%s", request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
    }

    /**
     * protocol://host[:port]/path?query#fragment
     * <p>
     * 示例：http://127.0.0.1:8080/demo/test?a=bbb <br>
     * 结果：http://127.0.0.1:8080
     */
    public static String getServerDomain(ServletRequest request) {
        return String.format("%s://%s:%d", request.getScheme(), request.getServerName(), request.getServerPort());
    }

    /**
     * 获取请求头
     *
     * @param request 请求
     * @return 请求头
     */
    public static Map<String, String[]> getHeaders(HttpServletRequest request) {
        Map<String, String[]> result = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String[] headers = Optional.ofNullable(request.getHeaders(headerName))
                    .map(Collections::list)
                    .map(list -> list.toArray(new String[0]))
                    .orElseGet(() -> new String[0]);
            result.put(headerName, headers);
        }
        return result;
    }

    @SneakyThrows
    public static byte[] getByteBody(ServletRequest request) {
        ServletInputStream inputStream = request.getInputStream();
        return IoUtil.readAll(inputStream, true);
    }

    @SneakyThrows
    public static char[] getCharBody(ServletRequest request) {
        BufferedReader reader = request.getReader();
        return IoUtil.readAll(reader, true);
    }

    public static int[] getCodePointBody(ServletRequest request) {
        String body = getStringBody(request);
        return body.codePoints().toArray();
    }

    public static String getStringBody(ServletRequest request) {
        char[] body = getCharBody(request);
        return new String(body);
    }

    protected RequestUtil() {
        throw new UnsupportedOperationException();
    }
}
