package org.zero.common.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/17
 */
@Slf4j
class MDCTest {
    ExecutorService threadPool = new ThreadPoolExecutor(10, 10, 1000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(100));

    @Test
    void test() throws InterruptedException {
        log.info("start test");
        String key = "test";
        MDC.put(key, key);
        for (int i = 0; i < 20; i++) {
            threadPool.execute(() -> {
                try {
                    int num = ThreadLocalRandom.current().nextInt(1, 1000);
                    // log.info("sleep time: {} ms", num);
                    // 线程池子线程
                    log.info("Pool sub thread MDC: {}", MDC.get(key));
                    // MDC.put(key, String.valueOf(num));
                    Thread.sleep(num);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        // 子线程
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                log.info("Sub thread MDC: {}", MDC.get(key));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        // 主线程
        log.info("Root thread MDC: {}", MDC.get(key));

        // 等待执行完毕
        threadPool.awaitTermination(3, TimeUnit.SECONDS);
    }
}
