package org.zero.common.test;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.threadpool.TtlExecutors;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/17
 */
class TtlTest {
    ExecutorService pool = Executors.newFixedThreadPool(1);
    ThreadLocal<String> threadLocal = new ThreadLocal<>();
    InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();
    TransmittableThreadLocal<String> transmittableThreadLocal = new TransmittableThreadLocal<>();
    String value = "Main Value";
    String updatedValue = "Main Value Updated";

    @Test
    void testThreadLocal() {
        System.out.println("\n--- 测试 ThreadLocal ---");

        // 主线程设置值
        threadLocal.set(value);

        // 直接创建的子线程
        new Thread(() -> System.out.printf("子线程[%s]的值：%s%n", Thread.currentThread(), threadLocal.get())).start();
        // 线程池中的子线程
        pool.submit(() -> System.out.printf("线程池子线程[%s]的值：%s%n", Thread.currentThread(), threadLocal.get()));

        // 主线程修改值
        threadLocal.set(updatedValue);
        new Thread(() -> System.out.printf("子线程[%s]的值：%s%n", Thread.currentThread(), threadLocal.get())).start();
        pool.submit(() -> System.out.printf("线程池复用子线程[%s]的值：%s%n", Thread.currentThread(), threadLocal.get()));
    }

    @Test
    void testInheritableThreadLocal() {
        System.out.println("\n--- 测试 InheritableThreadLocal ---");

        // 主线程设置值
        inheritableThreadLocal.set(value);

        // 直接创建的子线程
        new Thread(() -> System.out.printf("子线程[%s]的值：%s%n", Thread.currentThread(), inheritableThreadLocal.get())).start();
        // 线程池中的子线程
        pool.submit(() -> System.out.printf("线程池子线程[%s]的值：%s%n", Thread.currentThread(), inheritableThreadLocal.get()));

        // 主线程修改值
        inheritableThreadLocal.set(updatedValue);
        new Thread(() -> System.out.printf("子线程[%s]的值：%s%n", Thread.currentThread(), inheritableThreadLocal.get())).start();
        pool.submit(() -> System.out.printf("线程池复用子线程[%s]的值：%s%n", Thread.currentThread(), inheritableThreadLocal.get()));
    }

    @Test
    void testTransmittableThreadLocalWithApiTaskWrapper() {
        System.out.println("\n--- 测试 TransmittableThreadLocal（API模式 - 任务包装）---");

        // 主线程设置值
        transmittableThreadLocal.set(value);

        // 直接创建的子线程
        new Thread(TtlRunnable.get(() -> System.out.printf("子线程[%s]的值：%s%n", Thread.currentThread(), transmittableThreadLocal.get()))).start();
        // 线程池中的子线程
        pool.submit(TtlRunnable.get(() -> System.out.printf("线程池子线程[%s]的值：%s%n", Thread.currentThread(), transmittableThreadLocal.get())));

        // 主线程修改值
        transmittableThreadLocal.set(updatedValue);
        new Thread(TtlRunnable.get(() -> System.out.printf("子线程[%s]的值：%s%n", Thread.currentThread(), transmittableThreadLocal.get()))).start();
        pool.submit(TtlRunnable.get(() -> System.out.printf("线程池复用子线程[%s]的值：%s%n", Thread.currentThread(), transmittableThreadLocal.get())));
    }

    @Test
    void testTransmittableThreadLocalWithApiPoolWrapper() {
        System.out.println("\n--- 测试 TransmittableThreadLocal（API模式 - 线程池包装）---");
        ExecutorService pool = TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(1));

        // 主线程设置值
        transmittableThreadLocal.set(value);

        // 直接创建的子线程
        new Thread(() -> System.out.printf("子线程[%s]的值：%s%n", Thread.currentThread(), transmittableThreadLocal.get())).start();
        // 线程池中的子线程
        pool.submit(() -> System.out.printf("线程池子线程[%s]的值：%s%n", Thread.currentThread(), transmittableThreadLocal.get()));

        // 主线程修改值
        transmittableThreadLocal.set(updatedValue);
        new Thread(() -> System.out.printf("子线程[%s]的值：%s%n", Thread.currentThread(), transmittableThreadLocal.get())).start();
        pool.submit(() -> System.out.printf("线程池复用子线程[%s]的值：%s%n", Thread.currentThread(), transmittableThreadLocal.get()));
    }

    @Test
    void testTransmittableThreadLocalWithAgent() {
        System.out.println("\n--- 测试 TransmittableThreadLocal（Agent模式）---");
        System.err.println("注意：启动命令需要配置：-javaagent:path/to/transmittable-thread-local-2.14.2.jar");

        // 主线程设置值
        transmittableThreadLocal.set(value);

        // 直接创建的子线程
        new Thread(() -> System.out.printf("子线程[%s]的值：%s%n", Thread.currentThread(), transmittableThreadLocal.get())).start();
        // 线程池中的子线程
        pool.submit(() -> System.out.printf("线程池子线程[%s]的值：%s%n", Thread.currentThread(), transmittableThreadLocal.get()));

        // 主线程修改值
        transmittableThreadLocal.set(updatedValue);
        new Thread(() -> System.out.printf("子线程[%s]的值：%s%n", Thread.currentThread(), transmittableThreadLocal.get())).start();
        pool.submit(() -> System.out.printf("线程池复用子线程[%s]的值：%s%n", Thread.currentThread(), transmittableThreadLocal.get()));
    }
}
