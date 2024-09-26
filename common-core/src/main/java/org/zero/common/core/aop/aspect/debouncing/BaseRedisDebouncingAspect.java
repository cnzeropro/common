package org.zero.common.core.aop.aspect.debouncing;

import org.springframework.util.StringUtils;
import org.zero.common.core.util.spring.web.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
public abstract class BaseRedisDebouncingAspect extends BaseDebouncingAspect{
    /**
     * 缓存 key 的前缀
     */
    public static final String KEY_PREFIX = "sys:debouncing";

    @Override
    protected String getDefaultKey() {
        HttpServletRequest request = RequestUtil.getHttpServletRequest();
        if (Objects.isNull(request)) {
            return String.format("%s:%s", KEY_PREFIX, Thread.currentThread().getName());
        }
        String mark = this.getMark(request);
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
    protected String getMark(HttpServletRequest request) {
        return null;
    }

    @Override
    protected void afterInternal() {
        // do nothing
    }
}
