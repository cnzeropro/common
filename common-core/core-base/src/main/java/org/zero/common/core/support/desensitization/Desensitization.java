package org.zero.common.core.support.desensitization;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.zero.common.core.support.desensitization.jackson.DesensitizationSerializer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 脱敏注解
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/12
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@JacksonAnnotationsInside
@JsonSerialize(using = DesensitizationSerializer.class)
public @interface Desensitization {
    /**
     * 脱敏策略
     * <p>
     * <b>注意：仅当脱敏策略为 {@linkplain DesensitizationType#CUSTOM CUSTOM} 时，其余属性（{@link #start}、{@link #end}、{@link #replacement}）配置生效</b>
     */
    DesensitizationType type() default DesensitizationType.FIRST_MASK;

    /**
     * 脱敏开始位置（包含）
     * <p>
     * 从 0 开始，小于 0 时表示不脱敏
     */
    int start() default 0;

    /**
     * 脱敏结束位置（包含）
     * <p>
     * 可以为 0<br>
     * 小于 0 时，-n 表示：脱敏字串长度-n<br>
     * 如：-1 表示脱敏结束位置到整个字串长度
     */
    int end() default -1;

    /**
     * 脱敏填充字符
     * <p>
     * 为 null 时表示使用脱敏策略中默认填充字符
     */
    String replacement() default Desensitizer.DEFAULT_REPLACEMENT;
}
