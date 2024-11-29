package org.zero.job.manager.java;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/21
 */
class TaskManagerTest {
    TaskManager taskManager;
    CountDownLatch countDownLatch;

    @BeforeEach
    void before() {
        ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        taskManager = new TaskManager(threadPool);
        countDownLatch = new CountDownLatch(3);
    }

    @Test
    void test() throws InterruptedException {
        String key = "1";

        taskManager.trigger(1, () -> {
            try {
                Thread.sleep(500000000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.printf("run: %s%n", LocalDateTime.now());
        taskManager.schedule(key, () -> {
                    System.out.printf("%s %s %s%n", LocalDateTime.now(), Thread.currentThread(), "start");
                    // try {
                    //     Thread.sleep(500);
                    // } catch (InterruptedException e) {
                    //     throw new RuntimeException(e);
                    // }
                    System.out.printf("%s %s %s%n", LocalDateTime.now(), Thread.currentThread(), "end");
                    countDownLatch.countDown();
                },
                "0/3 * * * * ?");

        boolean running = taskManager.isRunning(key);
        System.out.printf("running: %s%n", running);

        countDownLatch.await();

        System.out.printf("stop: %s%n", LocalDateTime.now());
        taskManager.stop(key);
    }
}