package org.zero.common.core.extension.java.lang;

/**
 * 支持在收到 {@link InterruptedException} 后执行恢复逻辑并继续下一轮的循环模板。
 * <p>
 * 该类仅扩展“中断后可恢复重试”的语义；普通 {@link Throwable} 仍沿用
 * {@link LoopRunnable} 的 fail-fast 处理方式。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
public abstract class RetryOnInterruptLoopRunnable extends LoopRunnable {
	public RetryOnInterruptLoopRunnable(ThrowableRunnable runnable) {
		super(runnable);
	}

	@Override
	public void run() {
		while (this.shouldRunning()) {
			try {
				runnable.run();
				this.afterRunSuccess();
			} catch (InterruptedException e) {
				try {
					if (!this.shouldRetryAfterInterrupt(e)) {
						this.handleInterrupt(e);
						break;
					}
					this.beforeRetryAfterInterrupt(e);
				} catch (InterruptedException exception) {
					this.handleInterrupt(exception);
					break;
				} catch (Throwable throwable) {
					this.handleThrowable(throwable);
				}
			} catch (Throwable t) {
				this.handleThrowable(t);
			}
		}
	}

	/**
	 * 决定本次中断是否进入恢复流程。
	 * <p>
	 * 默认允许进入恢复流程。
	 */
	protected boolean shouldRetryAfterInterrupt(InterruptedException interruptedException) {
		return true;
	}

	/**
	 * 中断恢复钩子。
	 * <p>
	 * 子类应在这里执行退避、状态重置、重新订阅等恢复动作；正常返回后将继续下一轮循环。
	 */
	protected abstract void beforeRetryAfterInterrupt(InterruptedException interruptedException) throws InterruptedException;
}
