package org.zero.common.core.support.common.query.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;
import org.zero.common.data.model.query.BaseQO;

import java.util.Arrays;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/6
 */
public class StringToAliasArrayConverter implements Converter<String, BaseQO.Field[]> {
    @Override
    public BaseQO.Field[] convert(String source) {
        if (!StringUtils.hasText(source)) {
            return new BaseQO.Field[0];
        }
        return Arrays.stream(source.split(","))
                .map(field -> BaseQO.Field.create(field.trim()))
                .toArray(BaseQO.Field[]::new);
    }
}
