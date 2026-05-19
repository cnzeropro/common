package org.zero.common.core.extension.jakarta.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.zero.common.core.extension.jakarta.validation.internal.constraintvalidators.LandlineValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验座机号码格式。
 * <p>
 * 当前支持中国大陆普通座机号码，以及 400/800 服务号码。约束只校验非空白值的格式：{@code null}、空字符串及
 * 纯空白字符串视为未提供值并通过；必填语义请组合 {@code @NotBlank}。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/18
 */
@Target({
		ElementType.METHOD,
		ElementType.FIELD,
		ElementType.ANNOTATION_TYPE,
		ElementType.CONSTRUCTOR,
		ElementType.PARAMETER,
		ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = LandlineValidator.class)
public @interface Landline {

	/**
	 * 国际化消息模板。
	 * <p>
	 * 默认值使用 Bean Validation 消息占位符，消息键为 {@code jakarta.validation.constraints.Landline.message}。
	 * 本模块在 classpath 根路径提供 {@code ValidationMessages*.properties}，可被 Bean Validation 默认消息插值器读取。
	 *
	 * @return 消息模板
	 */
	String message() default "{jakarta.validation.constraints.Landline.message}";

	/**
	 * 约束分组。
	 *
	 * @return 约束分组
	 */
	Class<?>[] groups() default {};

	/**
	 * 约束负载。
	 *
	 * @return 约束负载
	 */
	Class<? extends Payload>[] payload() default {};
}
