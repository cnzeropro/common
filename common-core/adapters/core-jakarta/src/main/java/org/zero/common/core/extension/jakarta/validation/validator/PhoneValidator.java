package org.zero.common.core.extension.jakarta.validation.validator;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.PhoneUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 电话号码约束校验器。
 * <p>
 * 电话号码按手机号与座机号码的并集处理；空值与空白值保持可选字段语义，由 {@code @NotBlank} 等必填约束单独处理。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/18
 */
public class PhoneValidator implements ConstraintValidator<Phone, CharSequence> {
	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		// 格式约束只处理非空白值；需要必填时由 @NotBlank 组合表达。
		if (CharSequenceUtil.isBlank(value)) {
			return true;
		}
		return PhoneUtil.isMobile(value) ||
				PhoneUtil.isMobileHk(value) ||
				PhoneUtil.isMobileTw(value) ||
				PhoneUtil.isMobileMo(value) ||
				PhoneUtil.isTel(value) ||
				PhoneUtil.isTel400800(value);
	}
}
