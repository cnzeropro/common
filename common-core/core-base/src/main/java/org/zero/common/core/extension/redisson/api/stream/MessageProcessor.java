package org.zero.common.core.extension.redisson.api.stream;

/**
 * Stream 消息处理器。
 * <p>
 * {@link #process()} 表示消费循环中的一轮处理，而不是固定处理一条消息；具体实现可以选择阻塞等待新消息，
 * 也可以快速扫描一批待恢复消息后返回。普通异常默认交给外层消费循环处理。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/28
 */
@FunctionalInterface
public interface MessageProcessor {
	/**
	 * 执行一轮消息处理。
	 */
	void process();
}
