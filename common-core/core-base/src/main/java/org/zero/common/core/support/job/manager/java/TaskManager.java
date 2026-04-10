package org.zero.common.core.support.job.manager.java;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.zero.common.core.support.job.exception.TaskException;
import org.zero.common.core.support.job.manager.BaseTaskManager;
import org.zero.common.core.support.job.manager.java.cron.CronExpression;
import org.zero.common.core.support.job.model.ScheduledTaskHolder;
import org.zero.common.core.support.job.model.TriggeredCallableTaskHolder;
import org.zero.common.core.support.job.model.TriggeredRunnableTaskHolder;

import java.io.Serializable;
import java.text.ParseException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 基于 Java 原生调度 API 的任务管理器。
 * <p>
 * 依赖 {@link ScheduledExecutorService} 提供触发任务和 cron 调度能力。
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/7/20
 */
@Slf4j
@RequiredArgsConstructor
public class TaskManager extends BaseTaskManager {
	protected final ScheduledExecutorService scheduledExecutorService;

	/**
	 * 注册并执行一次性 Runnable 任务。
	 */
	@Override
	public void trigger(Serializable key, Runnable task) {
		this.registerTask(key, () -> {
			Future<?> future = scheduledExecutorService.submit(task);
			return TriggeredRunnableTaskHolder.builder()
				.future(future)
				.task(task)
				.build();
		});
		if (log.isDebugEnabled()) {
			log.debug("Registered triggered task[{}]", key);
		}
	}

	@Override
	public <T> Future<T> trigger(Serializable key, Callable<T> task) {
		AtomicReference<Future<T>> futureReference = new AtomicReference<Future<T>>();
		this.registerTask(key, () -> {
			Future<T> future = scheduledExecutorService.submit(task);
			futureReference.set(future);
			return TriggeredCallableTaskHolder.builder()
				.future(future)
				.task(task)
				.build();
		});
		if (log.isDebugEnabled()) {
			log.debug("Registered triggered task[{}]", key);
		}
		return futureReference.get();
	}

	/**
	 * 注册 cron 定时任务。
	 */
	@Override
	public void schedule(Serializable key, Runnable task, String cron, boolean continueAfterException) {
		CronExpression cronExpression;
		try {
			cronExpression = new CronExpression(cron);
		} catch (ParseException e) {
			throw new TaskException(String.format("Invalid cron expression [%s]", cron), e);
		}

		this.registerTask(key, () -> {
			Runnable wrappedTask = () -> {
				try {
					task.run();
				} catch (Throwable throwable) {
					log.error(
						"Scheduled task execution failed, key[{}], cron[{}], continueAfterException={}",
						key,
						cron,
						continueAfterException,
						throwable
					);
					if (!continueAfterException) {
						log.warn(
							"Scheduled task chain stopped after failure, key[{}], cron[{}], continueAfterException={}",
							key,
							cron,
							continueAfterException
						);
					}
					throw throwable;
				}
			};
			// 自定义续调包装器负责异常策略和下一次 cron 触发时间的重新计算。
			ReschedulingRunnable scheduledFuture = new ReschedulingRunnable(
				scheduledExecutorService,
				wrappedTask,
				cronExpression,
				continueAfterException
			).schedule();
			return ScheduledTaskHolder.builder()
				.future(scheduledFuture)
				.task(task)
				.cron(cron)
				.continueAfterException(continueAfterException)
				.build();
		});
		if (log.isDebugEnabled()) {
			log.debug(
				"Registered scheduled task[{}] with cron[{}], continueAfterException={}",
				key,
				cron,
				continueAfterException
			);
		}
	}
}
