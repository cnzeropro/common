package org.zero.common.core.extension.java.util;

/**
 * 键值对移除原因。
 * <p>
 * 该枚举描述 entry 离开当前逻辑映射的原因。监听器可据此区分系统自动清理、容量淘汰和调用方主动变更等场景。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/4
 */
public enum PurgeReason {
	/**
	 * 因容量上限或淘汰策略被驱逐。
	 */
	EVICTION,
	/**
	 * 因 TTL 到期被清理。
	 */
	EXPIRED,
	/**
	 * 因弱、软或虚引用被垃圾回收而清理。
	 * <p>
	 * 此时 listener 收到的 value 可能为 {@code null}。
	 */
	COLLECTED,
	/**
	 * 调用方显式删除单个 entry。
	 * <p>
	 * 典型来源包括 {@code remove(...)}、迭代器或集合视图删除，以及计算函数返回 {@code null}。
	 */
	EXPLICIT,
	/**
	 * 旧 entry 被新 entry 覆盖替换。
	 * <p>
	 * 典型来源是 {@code put(...)} 覆盖已有映射。
	 */
	REPLACED,
	/**
	 * 调用方通过 {@code clear()} 清空仍有效的 entry。
	 * <p>
	 * 如果清空时发现 entry 已经过期，仍会按 {@link #EXPIRED} 通知。
	 */
	CLEARED,
	;
}
