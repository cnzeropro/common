package org.zero.common.core.extension.jakarta.validation.internal.constraintvalidators;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.PhoneUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.zero.common.core.extension.jakarta.validation.constraints.Mobile;

/**
 * 手机号约束校验器。
 * <p>
 * 委托 Hutool {@link PhoneUtil} 判断中国大陆、香港、台湾、澳门手机号；空值与空白值保持可选字段语义，
 * 由 {@code @NotBlank} 等必填约束单独处理。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/18
 */
public class MobileValidator implements ConstraintValidator<Mobile, CharSequence> {
	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		// 格式约束只处理非空白值；需要必填时由 @NotBlank 组合表达。
		if (CharSequenceUtil.isBlank(value)) {
			return true;
		}
		return PhoneUtil.isMobile(value) ||
				PhoneUtil.isMobileHk(value) ||
				PhoneUtil.isMobileTw(value) ||
				PhoneUtil.isMobileMo(value);
	}
}
