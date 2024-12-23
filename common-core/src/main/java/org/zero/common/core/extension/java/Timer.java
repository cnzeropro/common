package org.zero.common.core.extension.java;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/23
 */
@Slf4j
public class Timer implements AutoCloseable {
    protected long startTime;
    protected long endTime;
    protected final String name;

    public static Timer start() {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        return new Timer(methodName);
    }

    public static Timer start(String name) {
        return new Timer(name);
    }

    public Timer end() {
        this.endTime = System.nanoTime();
        return this;
    }

    public Timer consume(Consumer<Timer> consumer) {
        this.end();
        consumer.accept(this);
        return this;
    }

    public Timer consume(BiConsumer<String, Duration> consumer) {
        this.end();
        consumer.accept(name, Duration.ofNanos(endTime - startTime));
        return this;
    }

    public Timer log() {
        return this.consume((name, duration) -> log.info("{} time consumption: {}", name, duration));
    }

    public Timer reset() {
        startTime = System.nanoTime();
        endTime = 0;
        return this;
    }

    @Override
    public void close() throws Exception {
        this.log().reset();
    }

    protected Timer(String name) {
        this.startTime = System.nanoTime();
        this.name = name;
    }
}
