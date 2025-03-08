package org.zero.common.core.util.javax.servlet;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/3
 */
public class RequestUtil {
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
}
