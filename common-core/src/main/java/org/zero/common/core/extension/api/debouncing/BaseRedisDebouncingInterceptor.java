package org.zero.common.core.extension.api.debouncing;

import org.springframework.util.StringUtils;
import org.zero.common.core.aop.aspect.debouncing.Debouncing;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/23
 */

public abstract class BaseRedisDebouncingInterceptor extends BaseDebouncingInterceptor {
    /**
     * 缓存 key 的前缀
     */
    public static final String KEY_PREFIX = "sys:api:debouncing";

    @Override
    protected String getDefaultKey(HttpServletRequest request, Debouncing debouncing) {
        String mark = this.getMark(request, debouncing);
        String requestMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        if (StringUtils.hasText(mark)) {
            return String.format("%s:%s:%s:%s", KEY_PREFIX, mark, requestMethod, requestURI);
        }
        return String.format("%s:%s:%s", KEY_PREFIX, requestMethod, requestURI);
    }

    /**
     * 获取防抖标识
     * <p>
     * 建议重写，可返回 token、用户名、客户端 ip 等等
     */
    protected String getMark(HttpServletRequest request, Debouncing debouncing) {
        return null;
    }
}
