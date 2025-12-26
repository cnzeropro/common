package org.zero.common.core.extension.java.util.concurrent;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/27
 */
@FunctionalInterface
public interface Task<V> extends Runnable, Callable<V>, Cloneable, Serializable {
	@Override
	default void run() {
		this.exec();
	}

	@Override
	default V call() {
		return this.exec();
	}

	V exec();
}
