package org.zero.common.core.support.api.debounce.annotation;

import org.zero.common.core.support.api.debounce.provider.DefaultMessageProvider;
import org.zero.common.core.support.api.debounce.provider.MessageProvider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 防抖
 *
 * @author zero
 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Debounce {
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
     * 此值是防抖的标识，如果缓存中存在此 key，则视为快速请求。<br>
     * 不建议自行配置，采用程序生成值即可。
     */
    String key() default "";

    /**
     * 缓存间隔时间，大于此时间不再视为快速请求。
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
