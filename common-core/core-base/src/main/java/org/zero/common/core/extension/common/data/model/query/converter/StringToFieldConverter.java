package org.zero.common.core.extension.common.data.model.query.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;
import org.zero.common.data.model.query.BaseQO;

/**
 * @author Zero (cnzeropro@163.com)
 * @see BaseQO.Field
 * @since 2025/1/6
 */
public class StringToFieldConverter implements Converter<String, BaseQO.Field> {
    @Override
    public BaseQO.Field convert(String source) {
        if (!StringUtils.hasText(source)) {
            return null;
        }
        return BaseQO.Field.create(source.trim());
    }
}
