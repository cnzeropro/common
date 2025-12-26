package org.zero.common.core.support.converter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
@FunctionalInterface
public interface ObjectConverter<T> extends GenericConverter<Object, T> {
	@Override
    T convert(Object source);

    default <U> ObjectConverter<T> compose(ObjectConverter<? extends U> before) {
        return source -> this.convert(before.convert(source));
    }

	default <U> ObjectConverter<U> andThen(ObjectConverter<? extends U> after) {
		return source -> after.convert(this.convert(source));
	}

    static ObjectConverter<Object> not() {
        return source -> source;
    }
}
