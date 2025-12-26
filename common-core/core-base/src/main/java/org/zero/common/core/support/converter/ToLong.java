package org.zero.common.core.support.converter;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class ToLong implements ObjectConverter<Long> {
    public static final ToLong INSTANCE = new ToLong();

    @Override
    public Long convert(Object source) {
        if (source instanceof Long){
            return (Long) source;
        }
        if (source instanceof Number){
            return ((Number) source).longValue();
        }
        if (Objects.nonNull(source)){
            return Long.valueOf(source.toString());
        }
        return null;
    }
}
