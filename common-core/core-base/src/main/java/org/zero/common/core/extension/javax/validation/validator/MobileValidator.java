package org.zero.common.core.extension.javax.validation.validator;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.PhoneUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MobileValidator implements ConstraintValidator<Mobile, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (CharSequenceUtil.isEmpty(value)) {
            return false;
        }
        return PhoneUtil.isMobile(value) || PhoneUtil.isMobileHk(value) || PhoneUtil.isMobileTw(value) || PhoneUtil.isMobileMo(value);
    }
}
