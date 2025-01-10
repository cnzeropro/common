package org.zero.common.core.extension.java.timer;

import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/26
 */
public abstract class BaseTimer<T extends BaseTimer<T>> implements AutoCloseable {
    protected final String name;
    protected long startTime;
    protected long endTime;

    public T end() {
        this.endTime = System.nanoTime();
        return this.self();
    }

    public T consume(Consumer<T> consumer) {
        this.end();
        T self = this.self();
        consumer.accept(self);
        return self;
    }

    public T consume(BiConsumer<String, Duration> consumer) {
        this.end();
        consumer.accept(name, Duration.ofNanos(endTime - startTime));
        return this.self();
    }

    public T reset() {
        endTime = 0;
        startTime = System.nanoTime();
        return this.self();
    }

    protected BaseTimer() {
        // Stack 1 -> getStackTrace
        // Stack 2 -> BaseTimer.<init>
        // xxx 表示 BaseTimer 的子类
        // Stack 3 -> xxx.<init>
        // xxx 表示具体方法
        // Stack 4 -> xxx
        this(Thread.currentThread().getStackTrace()[4].getMethodName());
    }

    protected BaseTimer(String name) {
        this.name = name;
        this.startTime = System.nanoTime();
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }
}
