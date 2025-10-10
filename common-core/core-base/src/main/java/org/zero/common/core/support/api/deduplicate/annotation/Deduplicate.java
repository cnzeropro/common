package org.zero.common.core.support.api.deduplicate.annotation;

import org.zero.common.core.support.api.deduplicate.provider.DefaultMessageProvider;
import org.zero.common.core.support.api.deduplicate.provider.MessageProvider;
import org.zero.common.core.support.api.deduplicate.voucher.DefaultEquivalentVoucher;
import org.zero.common.core.support.api.deduplicate.voucher.EquivalentVoucher;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 防重
 *
 * @author zero
 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Deduplicate {
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
     * 此值是防重的标识，如果缓存中存在此 key，则视为重复请求。<br>
     * 自行配置时，{@linkplain #equivalentVouchers 等效凭证} 将失效，因此不建配置，采用程序生成值即可。
     */
    String key() default "";

    /**
     * 间隔时间，大于此时间不再视为重复提交。
     * <p>
     * 默认：3000
     */
    long interval() default 3000L;

    /**
     * 时间单位。
     * <p>
     * 默认：毫秒
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 等效凭证，用于判别何为重复
     */
    Class<? extends EquivalentVoucher>[] equivalentVouchers() default {DefaultEquivalentVoucher.class};

    /**
     * 提示消息供给者。
     * <p>
     * 默认：{@link DefaultMessageProvider}
     * <p>
     * 阻止时用于生成错误信息提示
     */
    Class<? extends MessageProvider> messageProvider() default DefaultMessageProvider.class;
}
