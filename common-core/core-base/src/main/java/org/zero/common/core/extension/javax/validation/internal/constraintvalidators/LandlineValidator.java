package org.zero.common.core.extension.javax.validation.internal.constraintvalidators;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.PhoneUtil;
import org.zero.common.core.extension.javax.validation.constraints.Landline;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 座机号码约束校验器。
 * <p>
 * 委托 Hutool {@link PhoneUtil} 判断中国大陆普通座机号码与 400/800 服务号码；空值与空白值保持可选字段语义，
 * 由 {@code @NotBlank} 等必填约束单独处理。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/18
 */
public class LandlineValidator implements ConstraintValidator<Landline, CharSequence> {
	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		// 格式约束只处理非空白值；需要必填时由 @NotBlank 组合表达。
		if (CharSequenceUtil.isBlank(value)) {
			return true;
		}
		return PhoneUtil.isTel(value) || PhoneUtil.isTel400800(value);
	}
}
