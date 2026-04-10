package org.zero.common.core.support.job.manager.java;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.zero.common.core.support.job.enumeration.TaskType;
import org.zero.common.core.support.job.exception.TaskException;
import org.zero.common.core.support.job.model.ScheduledTaskHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Java 原生调度实现的回归测试。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/21
 */
class TaskManagerTest {
	private static final String EVERY_SECOND = "0/1 * * * * ?";
	private static final String EVERY_TWO_SECONDS = "0/2 * * * * ?";

	private ScheduledExecutorService scheduledExecutorService;
	private TaskManager taskManager;

	@BeforeEach
	void before() {
		// 线程池容量略大一些，避免并发测试时因为线程不足引入额外干扰。
		this.scheduledExecutorService = Executors.newScheduledThreadPool(Math.max(4, Runtime.getRuntime().availableProcessors()));
		this.taskManager = new TaskManager(this.scheduledExecutorService);
	}

	@AfterEach
	void after() throws InterruptedException {
		this.scheduledExecutorService.shutdownNow();
		this.scheduledExecutorService.awaitTermination(5, TimeUnit.SECONDS);
	}

	/**
	 * stop 只应取消底层 future，不应清空注册信息。
	 */
	@Test
	void stopShouldCancelTaskWithoutRemovingRegistration() throws InterruptedException {
		AtomicInteger executions = new AtomicInteger();
		CountDownLatch firstExecution = new CountDownLatch(1);

		this.taskManager.schedule("stop-job", () -> {
			executions.incrementAndGet();
			firstExecution.countDown();
		}, EVERY_SECOND);

		assertTrue(firstExecution.await(3, TimeUnit.SECONDS));

		this.taskManager.stop("stop-job");
		int currentExecutions = executions.get();

		this.sleep(1500);

		assertEquals(currentExecutions, executions.get());
		assertEquals(1, this.taskManager.count());
		assertEquals(TaskType.SCHEDULED_TASK, this.taskManager.getTaskType("stop-job"));
		assertNotNull(this.taskManager.getTaskFuture("stop-job"));
		assertFalse(this.taskManager.isRunning("stop-job"));

		this.taskManager.remove("stop-job");
	}

	/**
	 * remove 会同时取消任务并移除注册表记录。
	 */
	@Test
	void removeShouldCancelScheduledTask() throws InterruptedException {
		AtomicInteger executions = new AtomicInteger();
		CountDownLatch firstExecution = new CountDownLatch(1);

		this.taskManager.schedule("remove-job", () -> {
			executions.incrementAndGet();
			firstExecution.countDown();
		}, EVERY_SECOND);

		assertTrue(firstExecution.await(3, TimeUnit.SECONDS));

		this.taskManager.remove("remove-job");
		int currentExecutions = executions.get();

		this.sleep(1500);

		assertEquals(currentExecutions, executions.get());
		assertEquals(0, this.taskManager.count());
		assertEquals(TaskType.NONE, this.taskManager.getTaskType("remove-job"));
		assertFalse(this.taskManager.isRunning("remove-job"));
		assertNull(this.taskManager.getTaskFuture("remove-job"));
	}

	/**
	 * 同 key 重跑触发任务时，应先停止旧任务再启动新任务。
	 */
	@Test
	void reRunShouldRestartTriggeredTaskWithSameKey() throws InterruptedException {
		AtomicInteger executions = new AtomicInteger();
		CountDownLatch firstExecution = new CountDownLatch(1);
		CountDownLatch secondExecution = new CountDownLatch(1);

		this.taskManager.trigger("rerun-job", () -> {
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

		assertDoesNotThrow(() -> this.taskManager.reRun("rerun-job"));

		assertTrue(secondExecution.await(2, TimeUnit.SECONDS));
		assertTrue(executions.get() >= 2);
		assertEquals(TaskType.TRIGGERED_TASK, this.taskManager.getTaskType("rerun-job"));

		this.taskManager.remove("rerun-job");
	}

	/**
	 * 触发任务迁移到新 key 后，旧 key 应失效，新 key 应保留任务信息。
	 */
	@Test
	void reTriggerShouldMoveTaskToNewKey() throws InterruptedException {
		AtomicInteger executions = new AtomicInteger();
		CountDownLatch firstExecution = new CountDownLatch(1);
		CountDownLatch secondExecution = new CountDownLatch(1);

		this.taskManager.trigger("trigger-old", () -> {
			int current = executions.incrementAndGet();
			if (current == 1) {
				firstExecution.countDown();
			} else {
				secondExecution.countDown();
			}
		});

		assertTrue(firstExecution.await(2, TimeUnit.SECONDS));

		assertDoesNotThrow(() -> this.taskManager.reTrigger("trigger-old", "trigger-new"));

		assertTrue(secondExecution.await(2, TimeUnit.SECONDS));
		assertEquals(TaskType.NONE, this.taskManager.getTaskType("trigger-old"));
		assertEquals(TaskType.TRIGGERED_TASK, this.taskManager.getTaskType("trigger-new"));

		this.taskManager.remove("trigger-new");
	}

	/**
	 * 重新调度时应替换 cron，同时保留原始异常策略。
	 */
	@Test
	void reScheduleShouldReplaceCronAndKeepPolicy() throws InterruptedException {
		CountDownLatch firstExecution = new CountDownLatch(1);

		this.taskManager.schedule("reschedule-job", firstExecution::countDown, EVERY_SECOND, false);

		assertTrue(firstExecution.await(3, TimeUnit.SECONDS));

		assertDoesNotThrow(() -> this.taskManager.reSchedule("reschedule-job", EVERY_TWO_SECONDS));

		ScheduledTaskHolder holder = (ScheduledTaskHolder) this.taskManager.get("reschedule-job");
		assertEquals(EVERY_TWO_SECONDS, holder.getCron());
		assertFalse(holder.isContinueAfterException());
		assertEquals(TaskType.SCHEDULED_TASK, this.taskManager.getTaskType("reschedule-job"));
		assertTrue(this.taskManager.isRunning("reschedule-job"));

		this.taskManager.remove("reschedule-job");
	}

	/**
	 * Callable 触发任务返回的 Future 应能拿到真实结果。
	 */
	@Test
	void triggerCallableShouldReturnResult() throws InterruptedException, ExecutionException, TimeoutException {
		Future<Integer> future = this.taskManager.trigger("callable-job", () -> 7);

		assertEquals(Integer.valueOf(7), future.get(2, TimeUnit.SECONDS));
		assertEquals(TaskType.TRIGGERED_TASK, this.taskManager.getTaskType("callable-job"));

		this.taskManager.remove("callable-job");
	}

	/**
	 * 并发注册同 key 时，只允许一个成功，另一个应收到 TaskException，并产生日志。
	 */
	@Test
	void triggerShouldRejectConcurrentRegistrationWithSameKey() throws Exception {
		ExecutorService callerExecutor = Executors.newFixedThreadPool(2);
		CountDownLatch ready = new CountDownLatch(2);
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch taskStarted = new CountDownLatch(1);
		CountDownLatch releaseTask = new CountDownLatch(1);
		AtomicInteger executions = new AtomicInteger();

		try {
			Future<String> first = callerExecutor.submit(() -> this.submitTriggeredTask(ready, start, taskStarted, releaseTask, executions));
			Future<String> second = callerExecutor.submit(() -> this.submitTriggeredTask(ready, start, taskStarted, releaseTask, executions));

			assertTrue(ready.await(2, TimeUnit.SECONDS));
			start.countDown();

			String firstResult = first.get(3, TimeUnit.SECONDS);
			String secondResult = second.get(3, TimeUnit.SECONDS);

			assertTrue(taskStarted.await(2, TimeUnit.SECONDS));

			int startedCount = 0;
			int rejectedCount = 0;
			for (String result : new String[]{firstResult, secondResult}) {
				if ("started".equals(result)) {
					startedCount++;
				}
				if ("running".equals(result)) {
					rejectedCount++;
				}
			}

			releaseTask.countDown();
			this.sleep(200);

			assertEquals(1, startedCount);
			assertEquals(1, rejectedCount);
			assertEquals(1, executions.get());
		} finally {
			releaseTask.countDown();
			callerExecutor.shutdownNow();
			callerExecutor.awaitTermination(5, TimeUnit.SECONDS);
			this.taskManager.remove("concurrent-job");
		}
	}

	/**
	 * 对不存在任务调用 reRun 时，应抛出框架层 TaskException。
	 */
	@Test
	void reRunShouldThrowTaskExceptionWhenTaskDoesNotExist() {
		TaskException exception = assertThrows(TaskException.class, () -> this.taskManager.reRun("missing-job"));
		assertEquals("The task[missing-job] is not supported, task type: NONE", exception.getMessage());
	}

	/**
	 * 触发任务不允许走定时任务重调路径。
	 */
	@Test
	void reScheduleShouldThrowTaskExceptionWhenTaskIsTriggeredTask() {
		this.taskManager.trigger("type-mismatch-trigger", () -> {
		});

		try {
			TaskException exception = assertThrows(TaskException.class, () -> this.taskManager.reSchedule("type-mismatch-trigger"));
			assertEquals("The task[type-mismatch-trigger] is not scheduled task", exception.getMessage());
		} finally {
			this.taskManager.remove("type-mismatch-trigger");
		}
	}

	/**
	 * 定时任务不允许走触发任务重跑路径。
	 */
	@Test
	void reTriggerShouldThrowTaskExceptionWhenTaskIsScheduledTask() {
		this.taskManager.schedule("type-mismatch-schedule", () -> {
		}, EVERY_SECOND);

		try {
			TaskException exception = assertThrows(TaskException.class, () -> this.taskManager.reTrigger("type-mismatch-schedule"));
			assertEquals("The task[type-mismatch-schedule] is not triggered task", exception.getMessage());
		} finally {
			this.taskManager.remove("type-mismatch-schedule");
		}
	}

	/**
	 * 非法 cron 应在调度入口被包装成 TaskException，而不是直接暴露底层解析异常。
	 */
	@Test
	void scheduleShouldRejectInvalidCron() {
		TaskException exception = assertThrows(TaskException.class, () -> this.taskManager.schedule("invalid-cron-job", () -> {
		}, "invalid cron"));
		assertEquals("Invalid cron expression [invalid cron]", exception.getMessage());
		assertNotNull(exception.getCause());
		assertEquals(TaskType.NONE, this.taskManager.getTaskType("invalid-cron-job"));
	}

	/**
	 * 默认策略下，任务异常后仍应继续下一轮调度。
	 */
	@Test
	void scheduleShouldContinueAfterExceptionByDefault() throws InterruptedException {
		AtomicInteger executions = new AtomicInteger();
		CountDownLatch continuedExecution = new CountDownLatch(2);

		try {
			this.taskManager.schedule("continue-job", () -> {
				int current = executions.incrementAndGet();
				continuedExecution.countDown();
				if (current == 1) {
					throw new IllegalStateException("boom");
				}
			}, EVERY_SECOND);

			assertTrue(continuedExecution.await(4, TimeUnit.SECONDS));
			assertTrue(executions.get() >= 2);
			assertEquals(TaskType.SCHEDULED_TASK, this.taskManager.getTaskType("continue-job"));
			assertTrue(this.taskManager.isRunning("continue-job"));
		} finally {
			this.taskManager.remove("continue-job");
		}
	}

	/**
	 * 关闭“异常后继续”策略时，首次异常后应停止续调。
	 */
	@Test
	void scheduleShouldStopAfterExceptionWhenDisabled() throws InterruptedException {
		AtomicInteger executions = new AtomicInteger();
		CountDownLatch firstExecution = new CountDownLatch(1);

		try {
			this.taskManager.schedule("stop-on-error-job", () -> {
				executions.incrementAndGet();
				firstExecution.countDown();
				throw new IllegalStateException("boom");
			}, EVERY_SECOND, false);

			assertTrue(firstExecution.await(3, TimeUnit.SECONDS));

			this.sleep(1500);

			assertEquals(1, executions.get());
			assertEquals(TaskType.SCHEDULED_TASK, this.taskManager.getTaskType("stop-on-error-job"));
			assertFalse(this.taskManager.isRunning("stop-on-error-job"));
			assertNotNull(this.taskManager.getTaskFuture("stop-on-error-job"));
		} finally {
			this.taskManager.remove("stop-on-error-job");
		}
	}

	/**
	 * 任务注册表是实例级的，不同 TaskManager 之间不应共享 key 空间。
	 */
	@Test
	void taskManagersShouldNotShareTaskRegistry() throws Exception {
		ScheduledExecutorService anotherExecutor = Executors.newScheduledThreadPool(2);
		TaskManager anotherTaskManager = new TaskManager(anotherExecutor);
		CountDownLatch firstExecution = new CountDownLatch(1);
		CountDownLatch secondExecution = new CountDownLatch(1);

		try {
			this.taskManager.trigger("shared-key", () -> {
				firstExecution.countDown();
				try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException ignored) {
					Thread.currentThread().interrupt();
				}
			});

			assertTrue(firstExecution.await(2, TimeUnit.SECONDS));

			assertDoesNotThrow(() -> anotherTaskManager.trigger("shared-key", secondExecution::countDown));
			assertTrue(secondExecution.await(2, TimeUnit.SECONDS));

			assertEquals(TaskType.TRIGGERED_TASK, this.taskManager.getTaskType("shared-key"));
			assertEquals(TaskType.TRIGGERED_TASK, anotherTaskManager.getTaskType("shared-key"));
		} finally {
			this.taskManager.remove("shared-key");
			anotherTaskManager.remove("shared-key");
			anotherExecutor.shutdownNow();
			anotherExecutor.awaitTermination(5, TimeUnit.SECONDS);
		}
	}

	/**
	 * 同时发起两个注册请求，复用同一套同步器观察并发注册行为。
	 */
	private String submitTriggeredTask(
		CountDownLatch ready,
		CountDownLatch start,
		CountDownLatch taskStarted,
		CountDownLatch releaseTask,
		AtomicInteger executions
	) {
		ready.countDown();
		try {
			// 两个调用方都准备好后再同时放行，尽量放大并发注册窗口。
			assertTrue(start.await(2, TimeUnit.SECONDS));
			this.taskManager.trigger("concurrent-job", () -> {
				executions.incrementAndGet();
				taskStarted.countDown();
				try {
					assertTrue(releaseTask.await(5, TimeUnit.SECONDS));
				} catch (InterruptedException ignored) {
					Thread.currentThread().interrupt();
				}
			});
			return "started";
		} catch (TaskException ex) {
			return "running";
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			fail(ex);
			return "interrupted";
		}
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
				// 只做片段匹配，避免日志文案小幅调整导致测试过于脆弱。
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
