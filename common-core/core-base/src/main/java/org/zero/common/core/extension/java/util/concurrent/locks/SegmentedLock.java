package org.zero.common.core.extension.java.util.concurrent.locks;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 按 key 映射到底层段锁的分段锁契约。
 * <p>
 * 不同 key 可能因为哈希冲突竞争同一把底层锁，因此该接口提供的是“分段互斥”而非“一键一锁”语义。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/20
 */
public interface SegmentedLock {
	/**
	 * 基于 key 获取对应段锁。
	 *
	 * @param key 锁键
	 */
	void lock(Object key);

	/**
	 * 以可中断方式基于 key 获取对应段锁。
	 *
	 * @param key 锁键
	 * @throws InterruptedException 等待锁时线程被中断
	 */
	void lockInterruptibly(Object key) throws InterruptedException;

	/**
	 * 基于 key 获取对应段锁；若等待期间被中断，则继续重试直到成功，并在返回前恢复中断标记。
	 *
	 * @param key 锁键
	 */
	default void lockInterruptiblyRetryOnInterrupt(Object key) {
		boolean interrupted = false;
		try {
			while (true) {
				try {
					this.lockInterruptibly(key);
					return;
				} catch (InterruptedException e) {
					interrupted = true;
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * 尝试基于 key 获取对应段锁。
	 *
	 * @param key 锁键
	 * @return true 表示成功获取锁；false 表示当前未获取到锁
	 */
	boolean tryLock(Object key);

	/**
	 * 在限定时间内尝试基于 key 获取对应段锁。
	 *
	 * @param key  锁键
	 * @param time 等待时长
	 * @param unit 时间单位
	 * @return true 表示成功获取锁；false 表示超时仍未获取到锁
	 * @throws InterruptedException 等待锁时线程被中断
	 */
	boolean tryLock(Object key, long time, TimeUnit unit) throws InterruptedException;

	/**
	 * 在限定时间内尝试基于 key 获取对应段锁。
	 *
	 * @param key     锁键
	 * @param timeout 等待时长
	 * @return true 表示成功获取锁；false 表示超时仍未获取到锁
	 * @throws InterruptedException 等待锁时线程被中断
	 */
	default boolean tryLock(Object key, Duration timeout) throws InterruptedException {
		Duration actualTimeout = Objects.requireNonNull(timeout, "timeout");
		return this.tryLock(key, actualTimeout.toNanos(), TimeUnit.NANOSECONDS);
	}

	/**
	 * 基于 key 释放对应段锁。
	 *
	 * @param key 锁键
	 */
	void unlock(Object key);
}
