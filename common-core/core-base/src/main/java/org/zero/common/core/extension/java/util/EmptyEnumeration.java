package org.zero.common.core.extension.java.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * 共享的空 {@link Enumeration} 实现。
 * <p>
 * 该实现不产生任何元素，调用 {@link #nextElement()} 始终抛出 {@link NoSuchElementException}。
 *
 * @param <E> 元素类型
 * @author Zero (cnzeropro@163.com)
 * @see java.util.Collections#emptyEnumeration
 * @since 2025/4/27
 */
public final class EmptyEnumeration<E> implements Enumeration<E> {
	public static final Enumeration<?> INSTANCE = new EmptyEnumeration<>();

	/**
	 * 获取共享的空枚举实例。
	 *
	 * @param <T> 元素类型
	 * @return 空枚举实例
	 */
	@SuppressWarnings("unchecked")
	public static <T> Enumeration<T> getInstance() {
		return (Enumeration<T>) INSTANCE;
	}

	@Override
	public boolean hasMoreElements() {
		return false;
	}

	@Override
	public E nextElement() {
		throw new NoSuchElementException();
	}
}
