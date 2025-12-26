package org.zero.common.core.util.java.lang.ref;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

/**
 * 引用类型
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/3
 */
public enum ReferenceType {
	/**
	 * 强引用，对象不会被 GC 回收
	 */
	STRONG,
	/**
	 * 软引用，在 GC 报告内存不足时会被 GC 回收
	 */
	SOFT,
	/**
	 * 弱引用，在 GC 时发现弱引用会回收其对象
	 */
	WEAK,
	/**
	 * 虚引用，在 GC 时发现虚引用对象，会将 {@link PhantomReference} 插入 {@link ReferenceQueue}。
	 * <p>
	 * 此时对象未被真正回收，要等到 {@link ReferenceQueue} 被真正处理后才会被回收。
	 */
	PHANTOM
}
