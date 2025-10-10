package org.zero.common.core.support.api.idempotent.annotation;

import org.zero.common.core.support.api.idempotent.provider.DefaultMessageProvider;
import org.zero.common.core.support.api.idempotent.provider.MessageProvider;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口幂等
 *
 * @author zero
 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    /**
     * 是否启用。
     * <p>
     * 默认启用
     */
    boolean value() default true;

    /**
     * 提示消息供给者。
     * <p>
     * 默认：{@link DefaultMessageProvider}
     * <p>
     * 阻止时用于生成错误信息提示
     */
    Class<? extends MessageProvider> messageProvider() default DefaultMessageProvider.class;
}
