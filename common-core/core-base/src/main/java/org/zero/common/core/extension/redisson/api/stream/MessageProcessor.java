package org.zero.common.core.extension.redisson.api.stream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/28
 */
@FunctionalInterface
public interface MessageProcessor {
	void process();
}
