package org.zero.common.core.support.api.deduplication.annotation;

import org.zero.common.core.support.api.deduplication.provider.DeduplicationMessageProvider;
import org.zero.common.core.support.api.deduplication.provider.DefaultDeduplicationMessageProvider;
import org.zero.common.core.support.api.deduplication.voucher.DefaultEquivalentVoucher;
import org.zero.common.core.support.api.deduplication.voucher.EquivalentVoucher;

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
     * 间隔时间，小于此时间视为重复提交。默认：3000
     */
    long interval() default 3000L;

    /**
     * 时间单位。默认：毫秒
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 等效凭证，用于判别何为重复
     */
    Class<? extends EquivalentVoucher>[] equivalentVouchers() default {DefaultEquivalentVoucher.class};

    /**
     * 提示消息供给者
     */
    Class<? extends DeduplicationMessageProvider> messageProvider() default DefaultDeduplicationMessageProvider.class;
}
