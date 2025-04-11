package org.zero.common.core.support.api.throttle.annotation;

import org.zero.common.core.support.api.throttle.provider.DefaultMessageProvider;
import org.zero.common.core.support.api.throttle.provider.MessageProvider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 限流
 *
 * @author zero
 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Throttle {
    /**
     * 是否启用。
     * <p>
     * 默认启用
     */
    boolean value() default true;

    /**
     * 缓存 key，为空时采用程序生成值。
     * <p>
     * 默认为空
     * <p>
     * 不建议自行配置，采用程序生成值即可。
     */
    String key() default "";

    /**
     * 缓存最大值，超过最大值视为节流请求。
     * <p>
     * 默认：10
     */
    long limit() default 10L;

    /**
     * 缓存间隔时间，大于此时间视为新一轮节流周期。
     * <p>
     * 默认：1000
     */
    long interval() default 1000L;

    /**
     * 缓存时间单位。
     * <p>
     * 默认：毫秒
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 提示消息供给者。
     * <p>
     * 默认：{@link DefaultMessageProvider}
     * <p>
     * 阻止时用于生成错误信息提示
     */
    Class<? extends MessageProvider> messageProvider() default DefaultMessageProvider.class;
}
