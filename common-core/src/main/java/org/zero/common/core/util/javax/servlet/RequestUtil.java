package org.zero.common.core.util.javax.servlet;

import lombok.SneakyThrows;
import org.springframework.util.StringUtils;
import org.zero.common.core.util.java.io.IoUtil;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/3
 */
public class RequestUtil {
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
                            .flatMap(s -> Arrays.stream(StringUtils.commaDelimitedListToStringArray(s)))
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
     * 示例：http://127.0.0.1:8080/demo/test?a=bbb
     * 结果：http://127.0.0.1:8080/demo
     */
    public static String getDomain(HttpServletRequest request) {
        return String.format("%s://%s:%d%s", request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
    }

    /**
     * protocol://host[:port]/path?query#fragment
     * <p>
     * 示例：http://127.0.0.1:8080/demo/test?a=bbb
     * 结果：http://127.0.0.1:8080
     */
    public static String getServerDomain(HttpServletRequest request) {
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
    public static byte[] getByteBody(HttpServletRequest request) {
        ServletInputStream inputStream = request.getInputStream();
        return IoUtil.readAll(inputStream, true);
    }

    @SneakyThrows
    public static byte[] getStringBody(HttpServletRequest request) {
        BufferedReader reader = request.getReader();
        return IoUtil.readAll(inputStream, true);
    }

    protected RequestUtil() {
        throw new UnsupportedOperationException();
    }
}
