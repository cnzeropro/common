package org.zero.common.core.extension.java.util.concurrent;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/14
 */
@RequiredArgsConstructor
@Log
public class NewThreadRunRejectedExecutionHandler implements RejectedExecutionHandler {
    private static final AtomicLong COUNTER = new AtomicLong();
    private final String jobName;

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.log(Level.WARNING, "ThreadPool[{0}] overload, the task[{1}] will run by a new thread! Maybe you need to adjust the ThreadPool config!", new Object[]{executor, r});
        if (!executor.isShutdown()) {
            String threadName = jobName + "-" + COUNTER.getAndIncrement();
            log.log(Level.INFO, "Create new thread[{0}] to run job", threadName);
            new Thread(r, threadName).start();
        }
    }
}