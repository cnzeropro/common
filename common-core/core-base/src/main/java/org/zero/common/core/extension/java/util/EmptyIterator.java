package org.zero.common.core.extension.java.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 共享的空 {@link Iterator} 实现。
 * <p>
 * 该实现不产生任何元素，调用 {@link #next()} 始终抛出 {@link NoSuchElementException}。
 *
 * @param <T> 元素类型
 * @author Zero (cnzeropro@163.com)
 * @see java.util.Collections#emptyIterator
 * @since 2025/4/27
 */
public final class EmptyIterator<T> implements Iterator<T> {
	public static final Iterator<?> INSTANCE = new EmptyIterator<>();

	/**
	 * 获取共享的空迭代器实例。
	 *
	 * @param <T> 元素类型
	 * @return 空迭代器实例
	 */
	@SuppressWarnings("unchecked")
	public static <T> Iterator<T> getInstance() {
		return (Iterator<T>) INSTANCE;
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public T next() {
		throw new NoSuchElementException();
	}
}
