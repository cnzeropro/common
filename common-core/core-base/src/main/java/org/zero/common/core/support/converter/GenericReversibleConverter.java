package org.zero.common.core.support.converter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/10
 */
public interface GenericReversibleConverter<T, R> extends ReversibleConverter {
	R convert(T source);

	T reverse(R source);
}
