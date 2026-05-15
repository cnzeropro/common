package org.zero.common.core.extension.java.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;

/**
 * 将 {@link Enumeration} 适配为一次性 {@link Iterable}。
 * <p>
 * 该类型不会缓存或复制元素。每次调用 {@link #iterator()} 都会创建新的 {@link EnumerationIterator}，
 * 但这些迭代器共享同一个底层 {@code Enumeration} 游标。首次遍历会消费原始枚举，后续遍历只会从剩余位置继续，
 * 通常为空。因此该类型只适合临时传递给需要 {@code Iterable} 的消费型 API。传入 {@code null} 时按空枚举处理。
 *
 * @param <E> 元素类型
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/18
 */
public final class EnumerationIterable<E> implements Iterable<E> {
	private final Enumeration<E> enumeration;

	public EnumerationIterable(Enumeration<E> enumeration) {
		this.enumeration = Objects.nonNull(enumeration) ? enumeration : EmptyEnumeration.getInstance();
	}

	/**
	 * 创建 {@link Enumeration} 到一次性 {@link Iterable} 的适配器。
	 *
	 * @param enumeration 待适配枚举；为 {@code null} 时按空枚举处理
	 * @param <E>         元素类型
	 * @return 枚举可迭代适配器
	 */
	public static <E> EnumerationIterable<E> of(Enumeration<E> enumeration) {
		return new EnumerationIterable<>(enumeration);
	}

	@Override
	public Iterator<E> iterator() {
		return EnumerationIterator.of(this.enumeration);
	}
}
