package org.zero.common.core.extension.api.deduplication;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 接口防重
 *
 * @author zero
 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Deduplication {
    /**
     * 是否启用。默认：true
     */
    boolean value() default true;

    /**
     * 缓存 key，为空时采用程序生成值。默认为空
     */
    String key() default "";

    /**
     * 间隔时间，小于此时间视为重复提交。默认：1000
     */
    long interval() default 1000L;

    /**
     * 时间单位。默认：毫秒
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    EquivalentVoucherType[] equivalentVoucherTypes() default {EquivalentVoucherType.REQUEST_METHOD, EquivalentVoucherType.REQUEST_URI, EquivalentVoucherType.REQUEST_PARAMS, EquivalentVoucherType.REQUEST_BODY};

    /**
     * 提示消息
     */
    String message() default "重复提交";
}
