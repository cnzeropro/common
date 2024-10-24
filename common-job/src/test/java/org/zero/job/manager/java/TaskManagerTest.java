package org.zero.job.manager.java;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/21
 */
class TaskManagerTest {
    TaskManager taskManager = new TaskManager(Executors.newScheduledThreadPool(10));

    @Test
    void test() throws InterruptedException {
        taskManager.schedule("1", () -> {
            System.out.printf("%s-%s-%s%n", Thread.currentThread(), "1", LocalDateTime.now());
            // try {
            //     Thread.sleep(500);
            // } catch (InterruptedException e) {
            //     throw new RuntimeException(e);
            // }
            System.out.printf("%s-%s-%s%n", Thread.currentThread(), "2", LocalDateTime.now());
        }, "0/3 * * * * ?");
        System.out.println(LocalDateTime.now() + " start");
        boolean running = taskManager.isRunning("1");
        System.out.println(LocalDateTime.now() + " running=" + running);
        Thread.sleep(10 * 1000);
        taskManager.stop("1");
        System.out.println(LocalDateTime.now() + " stop");
        Thread.sleep(10 * 1000);
    }
}