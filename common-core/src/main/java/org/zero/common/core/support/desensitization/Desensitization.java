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
     * <b>注意：仅当脱敏策略为 CUSTOM 时，以下属性（start，end，value）配置生效</b>
     */
    DesensitizationType type() default DesensitizationType.CUSTOM;

    /**
     * 脱敏开始位置
     * <p>
     * 小于 0 时表示不脱敏
     */
    int start() default 0;

    /**
     * 脱敏结束位置
     * <p>
     * 小于 0 时，-n 表示“脱敏字串长度-n”，如：-1 表示脱敏结束位置到整个字串长度
     */
    int end() default -1;

    /**
     * 脱敏填充字符
     * <p>
     * 为空时表示使用脱敏策略中默认填充字符
     */
    String value() default "*";
}
