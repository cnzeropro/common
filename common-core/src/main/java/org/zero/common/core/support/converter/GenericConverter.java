package org.zero.common.core.support.converter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
@FunctionalInterface
public interface GenericConverter<T> extends Converter {
    T convert(Object source);

    default <U> GenericConverter<U> andThen(GenericConverter<? extends U> after) {
        return source -> after.convert(convert(source));
    }

    default <U> GenericConverter<T> compose(GenericConverter<? super U> before) {
        return source -> convert(before.convert(source));
    }

    static GenericConverter<Object> not() {
        return source -> source;
    }
}
