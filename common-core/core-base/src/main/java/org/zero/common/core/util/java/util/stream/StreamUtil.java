package org.zero.common.core.util.java.util.stream;

import org.zero.common.core.extension.java.util.EmptyIterator;
import org.zero.common.core.extension.java.util.EnumerationIterator;
import org.zero.common.core.util.java.lang.ArrayUtil;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * {@link Stream} 构造工具。
 * <p>
 * {@link Iterator} 与 {@link Enumeration} 重载按一次性游标处理，返回的流会消费原始游标。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/27
 */
public class StreamUtil {
	public static <T> Stream<T> of(T[] array) {
		return of(array, false);
	}

	public static <T> Stream<T> of(T[] array, boolean parallel) {
		Spliterator<T> spliterator = Spliterators.spliterator(
				Objects.isNull(array) ? ArrayUtil.EMPTY : array,
				Spliterator.ORDERED | Spliterator.IMMUTABLE
		);
		return StreamSupport.stream(spliterator, parallel);
	}

	public static <T> Stream<T> of(Enumeration<T> enumeration) {
		return of(enumeration, false);
	}

	public static <T> Stream<T> of(Enumeration<T> enumeration, boolean parallel) {
		Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(
				EnumerationIterator.of(enumeration),
				Spliterator.ORDERED
		);
		return StreamSupport.stream(spliterator, parallel);
	}

	public static <T> Stream<T> of(Iterator<T> iterator) {
		return of(iterator, false);
	}

	public static <T> Stream<T> of(Iterator<T> iterator, boolean parallel) {
		Iterator<T> actualIterator = Objects.nonNull(iterator) ? iterator : EmptyIterator.getInstance();
		Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(actualIterator, Spliterator.ORDERED);
		return StreamSupport.stream(spliterator, parallel);
	}

	public static <T> Stream<T> of(Iterable<T> iterable) {
		return of(iterable, false);
	}

	public static <T> Stream<T> of(Iterable<T> iterable, boolean parallel) {
		if (iterable instanceof Collection) {
			Collection<T> collection = (Collection<T>) iterable;
			return parallel ? collection.parallelStream() : collection.stream();
		}
		return StreamSupport.stream(iterable.spliterator(), parallel);
	}
}
