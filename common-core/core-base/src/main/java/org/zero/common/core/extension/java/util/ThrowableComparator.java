package org.zero.common.core.extension.java.util;

import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.util.Comparator;

/**
 * 允许抛出 {@link Throwable} 的 {@link Comparator} 变体。
 * <p>
 * 可通过 {@link #to()} 适配为标准 {@link Comparator}。适配后的比较器不会吞掉异常，
 * 而是通过 sneaky throw 继续向调用方传播。
 *
 * @author Zero (cnzeropro@163.com)
 * @see Comparator
 * @see ThrowableUtil
 * @since 2025/5/8
 */
@FunctionalInterface
public interface ThrowableComparator<T> {
	/**
	 * 将标准 {@link Comparator} 包装为可抛异常比较器。
	 *
	 * @param comparator 标准比较器
	 * @param <T>        待比较对象类型
	 * @return 可抛异常比较器
	 */
	static <T> ThrowableComparator<T> of(Comparator<T> comparator) {
		return comparator::compare;
	}

	/**
	 * 比较两个对象的顺序，允许抛出任意 {@link Throwable}。
	 * <p>
	 * 返回值语义与 {@link Comparator#compare(Object, Object)} 保持一致：
	 * 小于零表示第一个对象排在第二个对象之前，等于零表示顺序相同，大于零表示之后。
	 *
	 * @param o1 第一个待比较对象
	 * @param o2 第二个待比较对象
	 * @return 比较结果
	 * @throws Throwable 比较过程中产生的异常或错误
	 */
	int compare(T o1, T o2) throws Throwable;

	/**
	 * 适配为标准 {@link Comparator}。
	 * <p>
	 * {@link #compare(Object, Object)} 抛出的异常会通过 {@link ThrowableUtil#throwUnchecked(Throwable)}
	 * 继续传播，适用于只能接收标准 {@link Comparator} 的 JDK API。
	 *
	 * @return 标准比较器
	 */
	default Comparator<T> to() {
		return (o1, o2) -> {
			try {
				return this.compare(o1, o2);
			} catch (Throwable throwable) {
				throw ThrowableUtil.throwUnchecked(throwable);
			}
		};
	}
}
