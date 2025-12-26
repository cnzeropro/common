package org.zero.common.core.support.converter;

import java.util.function.Function;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/11
 */
@FunctionalInterface
public interface GenericConverter<T, R> extends Function<T, R>, Converter {
	R convert(T source);

	@Override
	default R apply(T t) {
		return this.convert(t);
	}

	default <V> GenericConverter<V, R> compose(GenericConverter<? super V, ? extends T> before) {
		return source -> this.apply(before.apply(source));
	}

	default <V> GenericConverter<T, V> andThen(GenericConverter<? super R, ? extends V> after) {
		return source -> after.apply(this.apply(source));
	}

	static <T> GenericConverter<T, T> not() {
		return source -> source;
	}
}
