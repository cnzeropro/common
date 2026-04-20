package org.zero.common.core.extension.java.util.concurrent.locks;

import org.zero.common.core.util.java.util.MapUtil;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于固定段数的分段锁实现。
 * <p>
 * 该实现会先将传入的期望并发数规整为不小于 1 的 2 的幂，然后创建对应数量的
 * {@link ReentrantLock} 作为底层段锁。不同的 key 可能因为哈希冲突映射到同一段，
 * 因此该类提供的是条带化互斥能力，而不是“一键一锁”语义。
 * <p>
 * 如需自定义 key 到段锁的映射，可通过覆写 {@link #resolveSegmentIndex(Object)}
 * 或 {@link #selectLock(Object)} 扩展。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/10
 */
public class ReentrantSegmentedLock implements SegmentedLock {
	protected final int mask;
	protected final Lock[] locks;

	/**
	 * 使用默认段数创建分段锁。
	 */
	public ReentrantSegmentedLock() {
		this(MapUtil.DEFAULT_INITIAL_CAPACITY);
	}

	/**
	 * 使用给定期望并发数创建非公平分段锁。
	 *
	 * @param concurrency 期望段数量；实际会被规整为不小于 1 的 2 的幂
	 */
	public ReentrantSegmentedLock(int concurrency) {
		this(concurrency, false);
	}

	/**
	 * 使用给定期望并发数创建分段锁。
	 *
	 * @param concurrency 期望段数量；实际会被规整为不小于 1 的 2 的幂
	 * @param fair        是否使用公平锁
	 */
	public ReentrantSegmentedLock(int concurrency, boolean fair) {
		int size = MapUtil.nextPowerOfTwo(concurrency);
		mask = size - 1;
		locks = new Lock[size];
		for (int i = 0; i < size; i++) {
			locks[i] = new ReentrantLock(fair);
		}
	}

	@Override
	public void lock(Object key) {
		this.selectLock(key).lock();
	}

	@Override
	public void lockInterruptibly(Object key) throws InterruptedException {
		this.selectLock(key).lockInterruptibly();
	}

	@Override
	public boolean tryLock(Object key) {
		return this.selectLock(key).tryLock();
	}

	@Override
	public boolean tryLock(Object key, long time, TimeUnit unit) throws InterruptedException {
		return this.selectLock(key).tryLock(time, unit);
	}

	@Override
	public void unlock(Object key) {
		this.selectLock(key).unlock();
	}

	/**
	 * 计算 key 所在的段索引。
	 *
	 * @param key 锁键
	 * @return 段索引
	 */
	protected int resolveSegmentIndex(Object key) {
		return Objects.hashCode(key) & mask;
	}

	/**
	 * 选择 key 对应的底层段锁。
	 *
	 * @param key 锁键
	 * @return 对应的段锁
	 */
	protected Lock selectLock(Object key) {
		int index = this.resolveSegmentIndex(key);
		return this.locks[index];
	}
}
