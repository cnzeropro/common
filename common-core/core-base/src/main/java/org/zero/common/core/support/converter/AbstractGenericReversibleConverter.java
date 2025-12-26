package org.zero.common.core.support.converter;

import lombok.RequiredArgsConstructor;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/11
 */
@RequiredArgsConstructor
public abstract class AbstractGenericReversibleConverter<T, R> implements GenericReversibleConverter<T, R> {
	protected final GenericConverter<T, R> converter;
	protected final GenericConverter<R, T> reverseConverter;

	@Override
	public R convert(T source) {
		return converter.convert(source);
	}

	@Override
	public T reverse(R source) {
		return reverseConverter.convert(source);
	}
}
