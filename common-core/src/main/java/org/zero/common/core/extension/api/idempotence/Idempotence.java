package org.zero.common.core.extension.api.idempotence;

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
public @interface Idempotence {
    /**
     * 是否启用。默认：true
     */
    boolean value() default true;

    /**
     * 提示消息
     */
    String message() default "请求已提交";
}
