package org.zero.common.core.extension.java.lang;

import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.lang.ThrowableUtil;

/**
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

	protected void handleInterrupt(InterruptedException interruptedException) {
		Thread.currentThread().interrupt();
	}

	protected void handleThrowable(Throwable throwable) {
		throw ThrowableUtil.throwUnchecked(throwable);
	}
}
