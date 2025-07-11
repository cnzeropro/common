package org.zero.common.core.extension.java.util.concurrent.locks;

import org.zero.common.core.util.java.util.MapUtil;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/10
 */
public class SegmentLock {
    protected final int mask;
    protected final Lock[] locks;

    public SegmentLock() {
        this(MapUtil.DEFAULT_INITIAL_CAPACITY);
    }

    public SegmentLock(int concurrency) {
        this(concurrency, false);
    }

    public SegmentLock(int concurrency, boolean fair) {
        int size = MapUtil.nextPowerOfTwo(concurrency);
        mask = size - 1;
        locks = new Lock[size];
        for (int i = 0; i < size; i++) {
            locks[i] = new ReentrantLock(fair);
        }
    }

    public void lock(Object lockObj) {
        int lockId = this.generateLockId(lockObj);
        this.lock(lockId);
    }

    public void lock(int lockId) {
        int index = index(lockId);
        locks[index].lock();
    }

    public void lockInterruptible(Object lockObj) throws InterruptedException {
        int lockId = this.generateLockId(lockObj);
        this.lockInterruptible(lockId);
    }

    public void lockInterruptible(int lockId) throws InterruptedException {
        int index = index(lockId);
        locks[index].lockInterruptibly();
    }

    public void lockInterruptibleSafe(Object lockObj) {
        int lockId = this.generateLockId(lockObj);
        this.lockInterruptibleSafe(lockId);
    }

    public void lockInterruptibleSafe(int lockId) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    this.lockInterruptible(lockId);
                    break;
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

    public void tryLock(Object lockObj) {
        int lockId = this.generateLockId(lockObj);
        this.tryLock(lockId);
    }

    public boolean tryLock(int lockId) {
        int index = index(lockId);
        return locks[index].tryLock();
    }

    public void tryLock(Object lockObj, long time, TimeUnit unit) throws InterruptedException {
        int lockId = this.generateLockId(lockObj);
        this.tryLock(lockId, time, unit);
    }

    public boolean tryLock(int lockId, long time, TimeUnit unit) throws InterruptedException {
        int index = index(lockId);
        return locks[index].tryLock(time, unit);
    }

    public void unlock(Object lockObj) {
        int lockId = this.generateLockId(lockObj);
        this.unlock(lockId);
    }

    public void unlock(int lockId) {
        int index = index(lockId);
        locks[index].unlock();
    }

    protected int generateLockId(Object lockObj) {
        return Objects.hashCode(lockObj);
    }

    protected int index(int lockId) {
        return lockId & mask;
    }
}
