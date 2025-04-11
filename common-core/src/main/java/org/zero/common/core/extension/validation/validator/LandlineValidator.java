package org.zero.common.core.extension.validation.validator;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.PhoneUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LandlineValidator implements ConstraintValidator<Landline, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (CharSequenceUtil.isEmpty(value)) {
            return false;
        }
        return PhoneUtil.isTel(value) || PhoneUtil.isTel400800(value);
    }
}
