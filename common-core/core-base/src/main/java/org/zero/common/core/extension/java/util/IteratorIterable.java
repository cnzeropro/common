package org.zero.common.core.extension.java.util;

import java.util.Iterator;
import java.util.Objects;

/**
 * 将 {@link Iterator} 适配为一次性 {@link Iterable}。
 * <p>
 * 该类型不会缓存或复制元素。{@link #iterator()} 始终返回构造时传入的同一个 {@code Iterator}，
 * 首次遍历会消费该迭代器，后续遍历只会从剩余位置继续，通常为空。因此该类型只适合临时传递给
 * 需要 {@code Iterable} 的消费型 API。传入 {@code null} 时按空迭代器处理。
 *
 * @param <T> 元素类型
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/24
 */
public final class IteratorIterable<T> implements Iterable<T> {
	private final Iterator<T> iterator;

	public IteratorIterable(Iterator<T> iterator) {
		this.iterator = Objects.nonNull(iterator) ? iterator : EmptyIterator.getInstance();
	}

	/**
	 * 创建 {@link Iterator} 到一次性 {@link Iterable} 的适配器。
	 *
	 * @param iterator 待适配迭代器；为 {@code null} 时按空迭代器处理
	 * @param <T>      元素类型
	 * @return 迭代器可迭代适配器
	 */
	public static <T> IteratorIterable<T> of(Iterator<T> iterator) {
		return new IteratorIterable<>(iterator);
	}

	@Override
	public Iterator<T> iterator() {
		return iterator;
	}
}
