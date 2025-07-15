package org.zero.common.core.extension.java.util.concurrent;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.zero.common.core.extension.java.lang.BaseBuilder;
import org.zero.common.core.util.java.security.SecurityUtil;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Zero (cnzeropro@163.com)
 * @see java.util.concurrent.Executors#defaultThreadFactory()
 * @since 2025/7/14
 */
public class DefaultThreadFactory implements ThreadFactory {
    protected static final ConcurrentMap<String, Long> POOL_NUMBER_MAP = new ConcurrentHashMap<>();
    protected final AtomicLong threadNumber = new AtomicLong(1L);
    protected final String jobName;
    protected final ThreadGroup group;
    protected final long stackSize;
    protected final int priority;
    protected final Boolean daemon;
    protected final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    protected final String namePattern;

    protected DefaultThreadFactory(String jobName, ThreadGroup group, long stackSize, int priority, Boolean daemon, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.jobName = jobName;
        this.group = group;
        this.stackSize = stackSize;
        this.daemon = daemon;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        this.priority = priority;
        long poolNumber = POOL_NUMBER_MAP.compute(jobName, (k, v) -> Objects.isNull(v) ? 1L : v + 1L);
        this.namePattern = "%s[pool-" + poolNumber + "-thread-%d]";
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, String.format(namePattern, jobName, threadNumber.getAndIncrement()), stackSize);
        if (Objects.nonNull(daemon)) {
            t.setDaemon(daemon);
        }
        t.setPriority(priority);
        t.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        return t;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends BaseBuilder<ThreadFactory, Builder> {
        protected String jobName = "UnknownJob";
        protected ThreadGroup group = this.defaultThreadGroup();
        protected long stackSize;
        protected int priority = Thread.NORM_PRIORITY;
        protected Boolean daemon;
        protected Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

        protected ThreadGroup defaultThreadGroup() {
            return SecurityUtil.IS_SECURITY_ENABLED ? SecurityUtil.SECURITY_MANAGER.getThreadGroup() : Thread.currentThread().getThreadGroup();
        }
    }
}
