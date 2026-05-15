package org.zero.common.core.extension.java.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;

/**
 * 将 {@link Enumeration} 适配为 {@link Iterator}。
 * <p>
 * 适配器不缓存、不复制元素；{@link #hasNext()} 与 {@link #next()} 直接代理到底层 {@code Enumeration}。
 * 因此调用 {@link #next()} 会推进原始枚举的游标。传入 {@code null} 时按空枚举处理。
 *
 * @param <E> 元素类型
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/18
 */
public final class EnumerationIterator<E> implements Iterator<E> {
	private final Enumeration<E> enumeration;

	public EnumerationIterator(Enumeration<E> enumeration) {
		this.enumeration = Objects.nonNull(enumeration) ? enumeration : EmptyEnumeration.getInstance();
	}

	/**
	 * 创建 {@link Enumeration} 到 {@link Iterator} 的适配器。
	 *
	 * @param enumeration 待适配枚举；为 {@code null} 时按空枚举处理
	 * @param <E>         元素类型
	 * @return 枚举迭代器适配器
	 */
	public static <E> EnumerationIterator<E> of(Enumeration<E> enumeration) {
		return new EnumerationIterator<>(enumeration);
	}

	@Override
	public boolean hasNext() {
		return enumeration.hasMoreElements();
	}

	@Override
	public E next() {
		return enumeration.nextElement();
	}
}
