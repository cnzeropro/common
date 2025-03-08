package org.zero.common.core.support.common.query.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;
import org.zero.common.data.model.query.BaseQO;

import java.util.Arrays;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/6
 */
public class StringToFieldArrayConverter implements Converter<String, BaseQO.Field[]> {
    @Override
    public BaseQO.Field[] convert(String source) {
        if (!StringUtils.hasText(source)){
            return new BaseQO.Field[0];
        }

        return Arrays.stream(StringUtils.commaDelimitedListToStringArray(source))
                .map(String::trim)
                .map(BaseQO.Field::create)
                .toArray(BaseQO.Field[]::new);
    }
}
