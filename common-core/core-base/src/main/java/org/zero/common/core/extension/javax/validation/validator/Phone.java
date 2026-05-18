package org.zero.common.core.extension.javax.validation.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验电话号码格式。
 * <p>
 * 电话号码是手机号与座机号码的并集。约束只校验非空白值的格式：{@code null}、空字符串及纯空白字符串
 * 视为未提供值并通过；必填语义请组合 {@code @NotBlank}。
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
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {

	/**
	 * 国际化消息模板。
	 * <p>
	 * 默认值使用 Bean Validation 消息占位符，消息键为 {@code javax.validation.validator.Phone.message}。
	 * 本模块在 classpath 根路径提供 {@code ValidationMessages*.properties}，可被 Bean Validation 默认消息插值器读取。
	 *
	 * @return 消息模板
	 */
	String message() default "{javax.validation.validator.Phone.message}";

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
