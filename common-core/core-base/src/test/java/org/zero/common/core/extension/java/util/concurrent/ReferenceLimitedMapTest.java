package org.zero.common.core.extension.java.util.concurrent;

import org.junit.jupiter.api.Test;
import org.zero.common.core.extension.java.util.LRUMap;
import org.zero.common.core.extension.java.util.PurgeReason;
import org.zero.common.core.extension.java.util.ReferenceLimitedMap;
import org.zero.common.core.util.java.lang.ref.ReferenceType;

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
        int count = 40;
        ReferenceLimitedMap<String, Object> map = ReferenceLimitedMap.<String, Object>builder()
                .maxCapacity(count / 2)
                .accessOrder(true)
                .lazyCleanup(false)
                .listener((key, value, reason) -> System.out.println("Purge [" + key + " -> " + value + "] from map, reason: " + reason + ", thread: " + Thread.currentThread().getName()))
                .build();
        for (int i = 0; i < count; i++) {
            map.put("k" + i, "v" + i);
        }

        System.out.println();
        map.put("key-ttl", "value", Duration.ofSeconds(5));
        Thread.sleep(4900);
        System.out.println("key-ttl in time: " + map.get("key-ttl"));
        Thread.sleep(500);
        System.out.println("key-ttl timeout: " + map.get("key-ttl"));
        map.put("key-weak", new BigDecimal("1"), ReferenceType.WEAK);
        System.out.println("key-weak: " + map.get("key-weak"));
        System.gc();
        System.out.println("key-weak gc: " + map.get("key-weak"));
        map.put("key-soft", new BigDecimal("2"), ReferenceType.SOFT);
        System.out.println("key-soft: " + map.get("key-soft"));
        map.put("key-phantom", new BigDecimal("3"), ReferenceType.PHANTOM);
        System.out.println("key-phantom: " + map.get("key-phantom"));
        map.put("key-weak-ttl", new BigDecimal("4"), ReferenceType.WEAK, Duration.ofMillis(1000));
        Thread.sleep(900);
        System.out.println("key-weak-ttl in time: " + map.get("key-weak-ttl"));
        Thread.sleep(100);
        System.out.println("key-weak-ttl timeout: " + map.get("key-weak-ttl"));
        System.gc();
        System.out.println("key-weak-ttl gc: " + map.get("key-weak-ttl"));
    }

    @Test
    void comparisonTest() {
        int count = 100_0000;
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
        map =  LRUMap.create(true);
        for (int i = 0; i < count; i++) {
            map.put("key" + i, "value" + i);
        }
        end = Instant.now();
        System.out.println(map.getClass() + " time-consuming: " + Duration.between(start, end));

        start = Instant.now();
        map = ReferenceLimitedMap.of(10000);
        for (int i = 0; i < count; i++) {
            map.put("key" + i, "value" + i);
        }
        end = Instant.now();
        System.out.println(map.getClass() + " time-consuming: " + Duration.between(start, end));
    }

    @Test
    void concurrencyTest() throws InterruptedException {
        int count = 100;
        // Map<Object, Object> map = ReferenceLimitedMap.of(Integer.MAX_VALUE);
        // Map<Object, Object> map = new ConcurrentHashMap<>();
        ReferenceLimitedMap<Object, Object> map = ReferenceLimitedMap.builder()
                .accessOrder(true)
                .lazyCleanup(false)
                .listener((key, value, reason) -> {
                    if (reason != PurgeReason.EVICTION)
                        System.out.println("Purge [" + key + " -> " + value + "] from map, reason: " + reason + ", thread: " + Thread.currentThread().getName());
                })
                .build();
        int putThreads = 1000;
        ExecutorService putExecutor = Executors.newFixedThreadPool(10);
        int getThreads = 500;
        ExecutorService getExecutor = Executors.newFixedThreadPool(10);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        CountDownLatch latch = new CountDownLatch(putThreads + getThreads);

        ReferenceType[] referenceTypes = ReferenceType.values();
        for (int i = 0; i < putThreads; i++) {
            putExecutor.submit(() -> {
                for (int j = 0; j < count; j++) {
                    Duration ttl = random.nextBoolean() ? null : Duration.ofSeconds(random.nextInt(10), random.nextInt(999_999_999));
                    ReferenceType referenceType = referenceTypes[random.nextInt(referenceTypes.length)];
                    // map.put(random.nextDouble(100000), random.nextInt(100000));
                    map.put(random.nextDouble(100000), random.nextInt(100000), referenceType, ttl);
                }
                latch.countDown();
            });
        }
        putExecutor.shutdown();

        for (int i = 0; i < getThreads; i++) {
            getExecutor.submit(() -> {
                for (int j = 0; j < count; j++) {
                    map.get(random.nextDouble(100000));
                }
                latch.countDown();
            });
        }
        getExecutor.shutdown();

        latch.await();
        System.out.println("size: " + map.size());
    }
}