package org.zero.common.core.extension.java.util.concurrent.locks;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/10
 */
class SegmentLockTest {
    @Test
    void test() throws InterruptedException {
        int lockId = 10086;
        SegmentLock lock = new SegmentLock(4);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.execute(() -> {
            System.out.println("before lock " + Thread.currentThread().getName());
            lock.lockInterruptibleSafe(lockId);
            System.out.println("acquire lock " + Thread.currentThread().getName());
            try {
                // 模拟业务耗时
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }finally {
                lock.unlock(lockId);
                System.out.println("release lock " + Thread.currentThread().getName());
            }
            System.out.println("after lock " + Thread.currentThread().getName());
        });
        pool.execute(() -> {
            System.out.println("before lock " + Thread.currentThread().getName());
            lock.lockInterruptibleSafe(lockId);
            System.out.println("acquire lock " + Thread.currentThread().getName());
            try {
                // 模拟业务耗时
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }finally {
                lock.unlock(lockId);
                System.out.println("release lock " + Thread.currentThread().getName());
            }
            System.out.println("after lock " + Thread.currentThread().getName());
        });
        pool.shutdown();

        Thread.sleep(5000);
    }
}