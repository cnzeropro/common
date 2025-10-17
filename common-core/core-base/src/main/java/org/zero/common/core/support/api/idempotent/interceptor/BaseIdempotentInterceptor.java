package org.zero.common.core.support.api.idempotent.interceptor;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.zero.common.core.extension.spring.web.servlet.interceptor.AbstractHandlerMethodInterceptor;
import org.zero.common.core.support.api.idempotent.annotation.Idempotent;
import org.zero.common.core.support.api.idempotent.provider.DefaultMessageProvider;
import org.zero.common.core.util.jackson.databind.JacksonUtils;
import org.zero.common.core.util.java.lang.reflect.MemberUtil;
import org.zero.common.core.util.javax.servlet.ResponseUtil;
import org.zero.common.data.model.view.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 幂等拦截器
 * <p>
 * 调用需要幂等的接口前，需要先请求获取幂等值的接口，该接口生成幂等值，并将其缓存到 Redis，
 * 然后在调用幂等接口时，可通过 header 等形式携带幂等值，服务端通过幂等值判断，如果两者相同，则请求通过，否则认为其非幂等请求不予通过。
 *
 * @author zero
 */
public abstract class BaseIdempotentInterceptor implements AbstractHandlerMethodInterceptor {
    /**
     * 缓存 key 的前缀
     */
    public static final String KEY_PREFIX = "sys:api:idempotent";
    public static final String IDEMPOTENCE_KEY_HEADER = "X-Idempotent-Key";
    public static final String IDEMPOTENCE_VALUE_HEADER = "X-Idempotent-Value";

    @Override
    public boolean supportsInternal(HandlerMethod handlerMethod) {
        return AnnotatedElementUtils.hasAnnotation(handlerMethod.getMethod(), Idempotent.class);
    }

    @Override
    public boolean preHandleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        Idempotent idempotent = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), Idempotent.class);
        if (Objects.isNull(idempotent)) {
            return true;
        }
        if (!idempotent.value()) {
            return true;
        }
        if (this.isPermit(request, handlerMethod, idempotent)) {
            return true;
        }
        String message = this.getMessage(request, handlerMethod, idempotent);
        Result<Void> result = Result.error(message);
        String jsonStr = JacksonUtils.toJsonStr(result);
        ResponseUtil.writeOkJson(response, jsonStr);
        return false;
    }

    /**
     * 是否放行
     */
    protected abstract boolean isPermit(HttpServletRequest request, HandlerMethod handlerMethod, Idempotent idempotent);

    /**
     * 获取幂等 key
     * <p>
     * 需要和生成幂等值的接口中生成的 key 逻辑一致。<br>
     * 建议从 header 里面取，如：{@link #KEY_PREFIX} + {@code request.getHeader("X-Idempotence-Key")}
     */
    protected String getKey(HttpServletRequest request, HandlerMethod handlerMethod, Idempotent idempotent) {
        String keyPrefix = this.getKeyPrefix(request, handlerMethod, idempotent);
        if (!StringUtils.hasText(keyPrefix)) {
            keyPrefix = KEY_PREFIX;
        }
        String headerName = this.getIdempotenceKeyHeaderName(request, handlerMethod, idempotent);
        if (!StringUtils.hasText(headerName)) {
            headerName = IDEMPOTENCE_KEY_HEADER;
        }
        String headerValue = request.getHeader(headerName);
        String isolationMark = this.getIsolationMark(request, handlerMethod, idempotent);
        if (StringUtils.hasText(isolationMark)) {
            return String.format("%s:%s:%s", keyPrefix, isolationMark, headerValue);
        }
        return String.format("%s:%s", keyPrefix, headerValue);
    }

    /**
     * 获取缓存 key 的前缀
     * <p>
     * 建议重写，可返回自定义前缀
     */
    protected String getKeyPrefix(HttpServletRequest request, HandlerMethod handlerMethod, Idempotent idempotent) {
        return KEY_PREFIX;
    }

    /**
     * 获取隔离标识
     * <p>
     * 可重写，返回 token、用户名、客户端 ip 等等作为隔离标识
     */
    protected String getIsolationMark(HttpServletRequest request, HandlerMethod handlerMethod, Idempotent idempotent) {
        return null;
    }

    protected String getIdempotenceKeyHeaderName(HttpServletRequest request, HandlerMethod handlerMethod, Idempotent idempotent) {
        return IDEMPOTENCE_KEY_HEADER;
    }

    /**
     * 获取幂等 value
     * <p>
     * 建议从 header 里面取，如：{@code request.getHeader("X-Idempotence-Value")}
     */
    protected Object getValue(HttpServletRequest request, HandlerMethod handlerMethod, Idempotent idempotent) {
        String headerName = this.getIdempotenceValueHeaderName(request, handlerMethod, idempotent);
        if (!StringUtils.hasText(headerName)) {
            headerName = IDEMPOTENCE_VALUE_HEADER;
        }
        return request.getHeader(headerName);
    }

    protected String getIdempotenceValueHeaderName(HttpServletRequest request, HandlerMethod handlerMethod, Idempotent idempotent) {
        return IDEMPOTENCE_VALUE_HEADER;
    }

    /**
     * 获取提示消息
     */
    protected String getMessage(HttpServletRequest request, HandlerMethod handlerMethod, Idempotent idempotent) {
        return MemberUtil.getInstanceOpt(idempotent.messageProvider())
                .map(messageProvider -> messageProvider.generate(handlerMethod, idempotent))
                .orElse(DefaultMessageProvider.MESSAGE);
    }
}
