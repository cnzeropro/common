package org.zero.common.core.support.timer;

import org.zero.common.core.util.java.lang.StackUtil;

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

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    protected BaseTimer() {

        this.name = createName();
        this.startTime = System.nanoTime();
    }

    /**
     * 创建名称
     * <pre>
     * Stack 1 -> getStackTrace
     * Stack 2 -> getCurrentStackTrace
     * Stack 3 -> createName
     * Stack 4 -> BaseTimer.<init>
     * // xxx 表示 BaseTimer 的子类
     * Stack 5 -> xxx.<init>
     * // xxx 表示具体方法
     * Stack 6 -> xxx
     * </pre>
     *
     * @return 名称
     */
    protected String createName() {
        int i = 0;
        for (StackTraceElement stackTraceElement : StackUtil.getCurrentStackTrace()) {
            String methodName = stackTraceElement.getMethodName();
            if (!methodName.contains("<init>")) {
                i++;
            }
            if (i > 4) {
                return methodName;
            }
        }
        return null;
    }

    protected BaseTimer(String name) {
        this.name = name;
        this.startTime = System.nanoTime();
    }
}
