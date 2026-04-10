package org.zero.common.core.support.job.manager;

import lombok.extern.slf4j.Slf4j;
import org.zero.common.core.support.job.enumeration.TaskType;
import org.zero.common.core.support.job.exception.TaskException;
import org.zero.common.core.support.job.model.FutureHolder;
import org.zero.common.core.support.job.model.ScheduledTaskHolder;
import org.zero.common.core.support.job.model.TriggeredCallableTaskHolder;
import org.zero.common.core.support.job.model.TriggeredRunnableTaskHolder;
import org.zero.common.core.support.job.model.TriggeredTaskHolder;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * 任务管理基类。
 * <p>
 * 统一封装触发任务、定时任务的注册、重跑和生命周期管理逻辑。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/23
 */
@Slf4j
public abstract class BaseTaskManager {
	/**
	 * 当前管理器实例下的任务注册表。
	 */
	protected final ConcurrentMap<Serializable, FutureHolder> taskMap = new ConcurrentHashMap<>();

	/**
	 * 串行化注册、重跑和删除流程，避免同 key 并发操作留下孤儿任务。
	 */
	protected final Object taskLock = new Object();

	/**
	 * 运行触发任务。
	 */
	public abstract void trigger(Serializable key, Runnable task);

	/**
	 * 运行触发任务，并返回固定结果。
	 */
	public <T> Future<T> trigger(Serializable key, Runnable task, T result) {
		Callable<T> callable = Executors.callable(task, result);
		return trigger(key, callable);
	}

	/**
	 * 运行触发任务。
	 */
	public abstract <T> Future<T> trigger(Serializable key, Callable<T> task);

	/**
	 * 重新触发任务。
	 * <p>
	 * 如当前任务仍在运行，会先取消旧任务，再按原 key 重建。
	 */
	public void reTrigger(Serializable key) {
		reTrigger(key, key);
	}

	/**
	 * 重新触发任务。
	 * <p>
	 * 先快照旧任务定义，再取消旧任务，最后按新 key 重建，避免旧任务继续运行。
	 */
	public void reTrigger(Serializable key, Serializable newKey) {
		synchronized (this.taskLock) {
			TriggeredTaskHolder futureHolder = this.requireTriggeredTask(key);
			this.cancelTask(futureHolder, true);
			this.removeTaskIfMoved(key, newKey);
			if (futureHolder instanceof TriggeredRunnableTaskHolder) {
				TriggeredRunnableTaskHolder triggeredRunnableTaskHolder = (TriggeredRunnableTaskHolder) futureHolder;
				trigger(newKey, triggeredRunnableTaskHolder.getTask());
			} else if (futureHolder instanceof TriggeredCallableTaskHolder) {
				TriggeredCallableTaskHolder triggeredCallableTaskHolder = (TriggeredCallableTaskHolder) futureHolder;
				trigger(newKey, triggeredCallableTaskHolder.getTask());
			} else {
				throw new TaskException(String.format("The task[%s] is not supported", key));
			}
		}
	}

	/**
	 * 运行定时任务。
	 */
	public void schedule(Serializable key, Runnable task, String cron) {
		schedule(key, task, cron, true);
	}

	/**
	 * 运行定时任务。
	 *
	 * @param continueAfterException 单次执行异常后，是否继续后续 cron 调度
	 */
	public abstract void schedule(Serializable key, Runnable task, String cron, boolean continueAfterException);

	/**
	 * 重新调度任务。
	 * <p>
	 * 如当前任务仍在运行，会先取消旧任务，再按原 key 重建。
	 */
	public void reSchedule(Serializable key) {
		reSchedule(key, key);
	}

	/**
	 * 重新调度任务。
	 * <p>
	 * 先快照旧任务定义，再取消旧任务，最后按新 key 重建，保留原 cron 和异常策略。
	 */
	public void reSchedule(Serializable key, Serializable newKey) {
		synchronized (this.taskLock) {
			ScheduledTaskHolder scheduledTaskHolder = this.requireScheduledTask(key);
			Runnable task = scheduledTaskHolder.getTask();
			String cron = scheduledTaskHolder.getCron();
			boolean continueAfterException = scheduledTaskHolder.isContinueAfterException();
			this.cancelTask(scheduledTaskHolder, true);
			this.removeTaskIfMoved(key, newKey);
			schedule(newKey, task, cron, continueAfterException);
		}
	}

	/**
	 * 重新调度任务。
	 * <p>
	 * 如当前任务仍在运行，会先取消旧任务，再按原 key 使用新 cron 重建。
	 */
	public void reSchedule(Serializable key, String cron) {
		reSchedule(key, key, cron);
	}

	/**
	 * 重新调度任务。
	 * <p>
	 * 先快照旧任务定义，再取消旧任务，最后按新 key 和新 cron 重建。
	 */
	public void reSchedule(Serializable key, Serializable newKey, String cron) {
		synchronized (this.taskLock) {
			ScheduledTaskHolder scheduledTaskHolder = this.requireScheduledTask(key);
			Runnable task = scheduledTaskHolder.getTask();
			boolean continueAfterException = scheduledTaskHolder.isContinueAfterException();
			this.cancelTask(scheduledTaskHolder, true);
			this.removeTaskIfMoved(key, newKey);
			schedule(newKey, task, cron, continueAfterException);
		}
	}

	/**
	 * 按任务类型重新运行任务。
	 * <p>
	 * 触发任务会走 {@link #reTrigger(Serializable, Serializable)}，
	 * 定时任务会走 {@link #reSchedule(Serializable, Serializable)}。
	 */
	public void reRun(Serializable key) {
		reRun(key, key);
	}

	/**
	 * 按任务类型重新运行任务。
	 */
	public void reRun(Serializable key, Serializable newKey) {
		TaskType taskType = this.getTaskType(key);
		if (taskType == TaskType.TRIGGERED_TASK) {
			reTrigger(key, newKey);
		} else if (taskType == TaskType.SCHEDULED_TASK) {
			reSchedule(key, newKey);
		} else {
			throw new TaskException(String.format("The task[%s] is not supported, task type: %s", key, taskType));
		}
	}

	/**
	 * 停止任务。
	 * <p>
	 * 此方法只取消底层 future，不会移除注册信息；如需移除，请使用 {@link #remove(Serializable)}。
	 */
	public void stop(Serializable key) {
		stop(key, true);
	}

	/**
	 * 停止任务。
	 * <p>
	 * 此方法只取消底层 future，不会移除注册信息；如需移除，请使用 {@link #remove(Serializable)}。
	 */
	public void stop(Serializable key, boolean mayInterruptIfRunning) {
		TaskType taskType;
		synchronized (this.taskLock) {
			FutureHolder futureHolder = this.taskMap.get(key);
			taskType = this.resolveTaskType(futureHolder);
			this.cancelTask(futureHolder, mayInterruptIfRunning);
		}
		if (log.isDebugEnabled()) {
			log.debug("Cancelled task[{}], type={}, mayInterruptIfRunning={}", key, taskType, mayInterruptIfRunning);
		}
	}

	/**
	 * 删除任务。
	 * <p>
	 * 先取消底层 future，再移除注册信息，避免后台任务继续运行但失去管理句柄。
	 */
	public void remove(Serializable key) {
		TaskType taskType;
		synchronized (this.taskLock) {
			FutureHolder futureHolder = this.taskMap.get(key);
			taskType = this.resolveTaskType(futureHolder);
			this.cancelTask(futureHolder, true);
			this.taskMap.remove(key);
		}
		if (log.isDebugEnabled()) {
			log.debug("Removed task[{}], type={}", key, taskType);
		}
	}

	/**
	 * 检查任务是否仍在运行中。
	 */
	public boolean isRunning(Serializable key) {
		Future<?> future = getTaskFuture(key);
		if (Objects.nonNull(future)) {
			return !future.isDone() && !future.isCancelled();
		}
		return false;
	}

	/**
	 * 获取任务的 {@link Future}。
	 */
	public Future<?> getTaskFuture(Serializable key) {
		FutureHolder futureHolder = get(key);
		if (Objects.nonNull(futureHolder)) {
			return futureHolder.getFuture();
		}
		return null;
	}

	/**
	 * 获取任务类型。
	 *
	 * @see TaskType
	 */
	public TaskType getTaskType(Serializable key) {
		FutureHolder futureHolder = get(key);
		return this.resolveTaskType(futureHolder);
	}

	/**
	 * 获取任务注册信息。
	 */
	public FutureHolder get(Serializable key) {
		return taskMap.get(key);
	}

	/**
	 * 获取任务总数。
	 * 已注册的所有任务都会计入，包括运行中、已完成和已取消的任务。
	 */
	public int count() {
		return taskMap.size();
	}

	/**
	 * 统一的注册入口。
	 * <p>
	 * 整个“检查状态 -> 创建 future -> 写入映射”流程必须保持原子性，避免并发重复提交。
	 */
	protected <T extends FutureHolder> T registerTask(Serializable key, Supplier<T> taskStarter) {
		synchronized (this.taskLock) {
			FutureHolder futureHolder = this.taskMap.get(key);
			if (this.isTaskRunning(futureHolder)) {
				TaskType taskType = this.resolveTaskType(futureHolder);
				log.warn("Rejected task registration for key[{}] because an active {} already exists", key, taskType);
				throw new TaskException(String.format("The task[%s] is running", key));
			}
			T newFutureHolder = taskStarter.get();
			this.taskMap.put(key, newFutureHolder);
			return newFutureHolder;
		}
	}

	/**
	 * 判断 holder 对应的底层 future 是否仍在运行。
	 */
	protected boolean isTaskRunning(FutureHolder futureHolder) {
		if (futureHolder == null || futureHolder.getFuture() == null) {
			return false;
		}
		Future<?> future = futureHolder.getFuture();
		return !future.isDone() && !future.isCancelled();
	}

	/**
	 * 取消底层 future。调用方负责决定是否移除注册信息。
	 */
	protected void cancelTask(FutureHolder futureHolder, boolean mayInterruptIfRunning) {
		if (futureHolder == null || futureHolder.getFuture() == null) {
			return;
		}
		futureHolder.getFuture().cancel(mayInterruptIfRunning);
	}

	/**
	 * 获取触发任务注册信息，不满足类型时直接抛错。
	 */
	protected TriggeredTaskHolder requireTriggeredTask(Serializable key) {
		FutureHolder futureHolder = this.taskMap.get(key);
		if (Objects.isNull(futureHolder)) {
			throw new TaskException(String.format("The task[%s] is not exist", key));
		}
		if (!(futureHolder instanceof TriggeredTaskHolder)) {
			throw new TaskException(String.format("The task[%s] is not triggered task", key));
		}
		return (TriggeredTaskHolder) futureHolder;
	}

	/**
	 * 获取定时任务注册信息，不满足类型时直接抛错。
	 */
	protected ScheduledTaskHolder requireScheduledTask(Serializable key) {
		FutureHolder futureHolder = this.taskMap.get(key);
		if (Objects.isNull(futureHolder)) {
			throw new TaskException(String.format("The task[%s] is not exist", key));
		}
		if (!(futureHolder instanceof ScheduledTaskHolder)) {
			throw new TaskException(String.format("The task[%s] is not scheduled task", key));
		}
		return (ScheduledTaskHolder) futureHolder;
	}

	/**
	 * 当任务迁移到新 key 时，移除旧 key 的注册信息。
	 */
	protected void removeTaskIfMoved(Serializable key, Serializable newKey) {
		if (!Objects.equals(key, newKey)) {
			this.taskMap.remove(key);
		}
	}

	/**
	 * 根据 holder 推断当前任务类型。
	 */
	protected TaskType resolveTaskType(FutureHolder futureHolder) {
		if (Objects.isNull(futureHolder)) {
			return TaskType.NONE;
		}
		if (futureHolder instanceof ScheduledTaskHolder) {
			return TaskType.SCHEDULED_TASK;
		}
		if (futureHolder instanceof TriggeredTaskHolder) {
			return TaskType.TRIGGERED_TASK;
		}
		return TaskType.UNKNOWN;
	}
}
