package org.zero.job.manager.spring;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.zero.job.enumeration.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Spring 调度实现的回归测试。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/26
 */
class TaskManagerTest {
    private static final String EVERY_SECOND = "0/1 * * * * ?";

    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private TaskManager taskManager;

    @BeforeEach
    void before() {
        // Spring 调度测试只需要少量线程即可覆盖异常续调和重跑场景。
        this.threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        this.threadPoolTaskScheduler.setPoolSize(2);
        this.threadPoolTaskScheduler.setThreadNamePrefix("common-job-spring-test-");
        this.threadPoolTaskScheduler.initialize();
        this.taskManager = new TaskManager(this.threadPoolTaskScheduler);
    }

    @AfterEach
    void after() {
        this.threadPoolTaskScheduler.shutdown();
    }

    /**
     * Spring 调度默认应在任务异常后继续后续 cron 触发。
     */
    @Test
    void scheduleShouldContinueAfterExceptionByDefaultAndWriteErrorLog() throws InterruptedException {
        ListAppender<ILoggingEvent> appender = this.attachAppender(TaskManager.class);
        AtomicInteger executions = new AtomicInteger();
        CountDownLatch continuedExecution = new CountDownLatch(2);

        try {
            this.taskManager.schedule("spring-continue-job", () -> {
                int current = executions.incrementAndGet();
                continuedExecution.countDown();
                if (current == 1) {
                    throw new IllegalStateException("boom");
                }
            }, EVERY_SECOND);

            assertTrue(continuedExecution.await(4, TimeUnit.SECONDS));
            assertTrue(executions.get() >= 2);
            assertEquals(TaskType.SCHEDULED_TASK, this.taskManager.getTaskType("spring-continue-job"));
            assertTrue(this.taskManager.isRunning("spring-continue-job"));
            assertTrue(this.awaitLog(appender, 1000, Level.ERROR, "spring-continue-job", EVERY_SECOND, "continueAfterException=true"));
        } finally {
            this.taskManager.remove("spring-continue-job");
            this.detachAppender(TaskManager.class, appender);
        }
    }

    /**
     * Spring 调度关闭“异常后继续”后，应在首次异常后终止后续触发。
     */
    @Test
    void scheduleShouldStopAfterExceptionWhenDisabledAndWriteWarnLog() throws InterruptedException {
        ListAppender<ILoggingEvent> appender = this.attachAppender(TaskManager.class);
        AtomicInteger executions = new AtomicInteger();
        CountDownLatch firstExecution = new CountDownLatch(1);

        try {
            this.taskManager.schedule("spring-stop-job", () -> {
                executions.incrementAndGet();
                firstExecution.countDown();
                throw new IllegalStateException("boom");
            }, EVERY_SECOND, false);

            assertTrue(firstExecution.await(3, TimeUnit.SECONDS));

            this.sleep(1500);

            assertEquals(1, executions.get());
            assertEquals(TaskType.SCHEDULED_TASK, this.taskManager.getTaskType("spring-stop-job"));
            assertFalse(this.taskManager.isRunning("spring-stop-job"));
            assertTrue(this.awaitLog(appender, 1000, Level.ERROR, "spring-stop-job", EVERY_SECOND, "continueAfterException=false"));
            assertTrue(this.awaitLog(appender, 1000, Level.WARN, "spring-stop-job", EVERY_SECOND, "continueAfterException=false"));
        } finally {
            this.taskManager.remove("spring-stop-job");
            this.detachAppender(TaskManager.class, appender);
        }
    }

    /**
     * remove 应取消调度并移除注册信息。
     */
    @Test
    void removeShouldCancelScheduledTask() throws InterruptedException {
        AtomicInteger executions = new AtomicInteger();
        CountDownLatch firstExecution = new CountDownLatch(1);

        this.taskManager.schedule("spring-remove-job", () -> {
            executions.incrementAndGet();
            firstExecution.countDown();
        }, EVERY_SECOND);

        assertTrue(firstExecution.await(3, TimeUnit.SECONDS));

        this.taskManager.remove("spring-remove-job");
        int currentExecutions = executions.get();

        this.sleep(1500);

        assertEquals(currentExecutions, executions.get());
        assertEquals(TaskType.NONE, this.taskManager.getTaskType("spring-remove-job"));
        assertFalse(this.taskManager.isRunning("spring-remove-job"));
    }

    /**
     * Spring 实现也应支持同 key 触发任务重跑。
     */
    @Test
    void reRunShouldRestartTriggeredTaskWithSameKey() throws InterruptedException {
        AtomicInteger executions = new AtomicInteger();
        CountDownLatch firstExecution = new CountDownLatch(1);
        CountDownLatch secondExecution = new CountDownLatch(1);

        this.taskManager.trigger("spring-rerun-job", () -> {
            int current = executions.incrementAndGet();
            if (current == 1) {
                firstExecution.countDown();
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
                return;
            }
            secondExecution.countDown();
        });

        assertTrue(firstExecution.await(2, TimeUnit.SECONDS));

        assertDoesNotThrow(() -> this.taskManager.reRun("spring-rerun-job"));

        assertTrue(secondExecution.await(2, TimeUnit.SECONDS));
        assertTrue(executions.get() >= 2);
        assertEquals(TaskType.TRIGGERED_TASK, this.taskManager.getTaskType("spring-rerun-job"));

        this.taskManager.remove("spring-rerun-job");
    }

    /**
     * Spring 触发任务的 Future 应返回原始 Callable 结果。
     */
    @Test
    void triggerCallableShouldReturnResult() throws Exception {
        Future<Integer> future = this.taskManager.trigger("spring-callable-job", () -> 9);

        assertEquals(Integer.valueOf(9), future.get(2, TimeUnit.SECONDS));
        assertEquals(TaskType.TRIGGERED_TASK, this.taskManager.getTaskType("spring-callable-job"));

        this.taskManager.remove("spring-callable-job");
    }

    /**
     * 为指定 logger 挂载内存 appender，用于断言关键日志。
     */
    private ListAppender<ILoggingEvent> attachAppender(Class<?> loggerType) {
        Logger logger = (Logger) LoggerFactory.getLogger(loggerType);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        return appender;
    }

    private void detachAppender(Class<?> loggerType, ListAppender<ILoggingEvent> appender) {
        Logger logger = (Logger) LoggerFactory.getLogger(loggerType);
        logger.detachAppender(appender);
        appender.stop();
    }

    /**
     * 异步任务日志有轻微延迟，这里轮询等待指定片段出现。
     */
    private boolean awaitLog(ListAppender<ILoggingEvent> appender, long timeoutMillis, Level level, String... fragments) {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMillis);
        while (System.nanoTime() <= deadline) {
            if (this.containsLog(appender, level, fragments)) {
                return true;
            }
            this.sleep(50);
        }
        return false;
    }

    private boolean containsLog(ListAppender<ILoggingEvent> appender, Level level, String... fragments) {
        List<ILoggingEvent> events = new ArrayList<ILoggingEvent>(appender.list);
        for (ILoggingEvent event : events) {
            if (!level.equals(event.getLevel())) {
                continue;
            }
            String message = event.getFormattedMessage();
            boolean matched = true;
            for (String fragment : fragments) {
                // 只断言关键上下文，避免日志文案小幅调整导致无意义失败。
                if (!message.contains(fragment)) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                return true;
            }
        }
        return false;
    }

    private void sleep(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            fail(ex);
        }
    }
}
