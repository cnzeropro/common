package org.zero.common.core.extension.java.lang;

import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.lang.ThrowableUtil;

/**
 * 可扩展的循环执行模板。
 * <p>
 * 默认适用于任务自身会阻塞等待的场景；若任务每轮可能快速返回，则应由子类在
 * {@link #afterRunSuccess()} 中自行增加退避或等待逻辑。
 * <p>
 * 当前实现对 {@link InterruptedException} 采用终止语义，对其他异常默认 fail-fast；
 * 若调用方需要“中断后恢复重试”的能力，应使用专用的
 * {@link RetryOnInterruptLoopRunnable}。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/14
 */
@RequiredArgsConstructor
public class LoopRunnable implements Runnable {
	protected final ThrowableRunnable runnable;

	@Override
	public void run() {
		while (this.shouldRunning()) {
			try {
				runnable.run();
				this.afterRunSuccess();
			} catch (InterruptedException e) {
				this.handleInterrupt(e);
				break;
			} catch (Throwable t) {
				this.handleThrowable(t);
			}
		}
	}

	protected boolean shouldRunning() {
		return !Thread.currentThread().isInterrupted();
	}

	/**
	 * 中断退出前的副作用处理。
	 * <p>
	 * 默认恢复当前线程的中断标记，循环随后退出。
	 */
	protected void handleInterrupt(InterruptedException interruptedException) {
		Thread.currentThread().interrupt();
	}

	/**
	 * 单轮任务成功执行后的扩展点。
	 */
	protected void afterRunSuccess() throws InterruptedException {
	}

	/**
	 * 普通异常处理。
	 * <p>
	 * 默认将异常继续抛出并结束线程；若确实需要恢复某类异常，应在专用子类中覆写。
	 */
	protected void handleThrowable(Throwable throwable) {
		throw ThrowableUtil.throwUnchecked(throwable);
	}
}
