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
     * Stack 0 -> {@link java.lang.Thread#getStackTrace()}
     * Stack 1 -> {@link StackUtil#getCurrentStackTrace()}
     * Stack 2 -> {@link #createName()}
     * // BaseTimer 的类构造器
     * Stack n -> &lt;init&gt;
     * // BaseTimer 的子类构造器
     * Stack n+1 -> &lt;init&gt;
     * // BaseTimer 的孙类构造器
     * Stack n+2 -> &lt;init&gt;
     * ......
     * // xxx 表示具体派生类的静态工厂方法，如：start
     * Stack 3 -> xxx
     * // xxx 表示上层调用的具体方法
     * Stack 4 -> xxx
     * </pre>
     *
     * @return 名称
     */
    protected String createName() {
        int i = 0;
        String methodName = null;
        for (StackTraceElement stackTraceElement : StackUtil.getCurrentStackTrace()) {
            String stackMethodName = stackTraceElement.getMethodName();
            if (i > 3) {
                methodName = stackMethodName;
                break;
            }
            if (!"<init>".equals(stackMethodName)) {
                i++;
            }
        }
        return String.format("Method[%s]", methodName);
    }

    protected BaseTimer(String name) {
        this.name = name;
        this.startTime = System.nanoTime();
    }
}
