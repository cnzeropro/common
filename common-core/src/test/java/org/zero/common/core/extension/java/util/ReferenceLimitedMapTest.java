package org.zero.common.core.extension.java.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/4
 */
class ReferenceLimitedMapTest {
    @Test
    void functionalTest() throws InterruptedException {
        int count = 100;
        PerEntryReferenceLimitedMap<String, Object> map = PerEntryReferenceLimitedMap.of(count / 2);
        for (int i = 0; i < count; i++) {
            map.put("k" + i, "v" + i);
        }

        System.out.println();
        map.put("key-ttl", "value", Duration.ofSeconds(5));
        Thread.sleep(4500);
        System.out.println("in time: " + map.get("key-ttl"));
        Thread.sleep(500);
        System.out.println("timeout: " + map.get("key-ttl"));
        map.put("key-weak", new BigDecimal("1"), ReferenceType.WEAK);
        System.out.println("weak: " + map.get("key-weak"));
        System.gc();
        System.out.println("weak: " + map.get("key-weak"));
        map.put("key-soft", new BigDecimal("2"), ReferenceType.SOFT, Duration.ofSeconds(1));
        System.out.println("soft: " + map.get("key-soft"));
        Thread.sleep(1000);
        System.out.println("soft: " + map.get("key-soft"));
        map.put("key-phantom", new BigDecimal("3"), ReferenceType.PHANTOM);
        System.out.println("phantom: " + map.get("key-phantom"));
    }

    @Test
    void comparisonTest() {
        int count = 10_0000;
        Instant start;
        Map<String, String> map;
        Instant end;

        start = Instant.now();
        map = new HashMap<>();
        for (int i = 0; i < count; i++) {
            map.put("key" + i, "value" + i);
        }
        end = Instant.now();
        System.out.println(map.getClass() + " time-consuming: " + Duration.between(start, end));

        start = Instant.now();
        map = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            map.put("key" + i, "value" + i);
        }
        end = Instant.now();
        System.out.println(map.getClass() + " time-consuming: " + Duration.between(start, end));

        start = Instant.now();
        map = new ConcurrentHashMap<>();
        for (int i = 0; i < count; i++) {
            map.put("key" + i, "value" + i);
        }
        end = Instant.now();
        System.out.println(map.getClass() + " time-consuming: " + Duration.between(start, end));

        start = Instant.now();
        map = new LRUCache<>(10000);
        for (int i = 0; i < count; i++) {
            map.put("key" + i, "value" + i);
        }
        end = Instant.now();
        System.out.println(map.getClass() + " time-consuming: " + Duration.between(start, end));

        start = Instant.now();
        map = PerEntryReferenceLimitedMap.of(1000);
        for (int i = 0; i < count; i++) {
            map.put("key" + i, "value" + i);
        }
        end = Instant.now();
        System.out.println(map.getClass() + " time-consuming: " + Duration.between(start, end));
    }

    @Test
    void concurrencyTest() throws InterruptedException {
        int threads = 100;
        int count = 100;
        Map<Object, Object> map = PerEntryReferenceLimitedMap.of(100);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threads);
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < threads; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < count; j++) {
                    map.put(random.nextDouble(100000), random.nextInt(100000));
                }
                latch.countDown();
            });
        }

        executorService.shutdown();
        latch.await();
    }
}