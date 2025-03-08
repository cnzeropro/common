package org.zero.job.manager.java;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.Executors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/21
 */
class TaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    void before() {
        taskManager = new TaskManager(Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()));
    }

    @Test
    void trigger() {
        System.out.printf("start: %s%n", LocalDateTime.now());
        taskManager.trigger("test", () -> {
            System.out.printf("%s %s %s%n", LocalDateTime.now(), Thread.currentThread(), "start");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.printf("%s %s %s%n", LocalDateTime.now(), Thread.currentThread(), "end");
        });

        // 阻塞主线程
        new Scanner(System.in).nextLine();
    }

    @Test
    void schedule() {
        System.out.printf("start: %s%n", LocalDateTime.now());
        taskManager.schedule("test", () -> {
                    System.out.printf("%s %s %s%n", LocalDateTime.now(), Thread.currentThread(), "start");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.printf("%s %s %s%n", LocalDateTime.now(), Thread.currentThread(), "end");
                },
                "0/3 * * * * ?");
        taskManager.schedule("test2", () -> {
                    System.out.printf("%s %s %s%n", LocalDateTime.now(), Thread.currentThread(), "start");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.printf("%s %s %s%n", LocalDateTime.now(), Thread.currentThread(), "end");
                },
                "0/5 * * * * ?");

        // 阻塞主线程
        new Scanner(System.in).nextLine();
    }
}