package org.zero.common.core.extension.redisson.api.stream;

import lombok.extern.slf4j.Slf4j;
import org.redisson.client.RedisTimeoutException;
import org.zero.common.core.extension.java.lang.LoopRunnable;
import org.zero.common.core.util.java.lang.ThreadUtil;

import java.time.Duration;

/**
 * Redisson Stream 消费循环。
 * <p>
 * 阻塞型处理器依赖 Redisson 读操作等待数据；轮询型处理器每轮成功后按配置退避。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/27
 */
@Slf4j
public class RedissonStreamConsumer extends LoopRunnable {
	protected final MessageProcessor processor;
	public static final Duration DEFAULT_SLEEP_TIME = Duration.ofSeconds(30);
	protected final Duration sleepTime;

	public RedissonStreamConsumer(MessageProcessor processor) {
		this(processor, DEFAULT_SLEEP_TIME);
	}

	public RedissonStreamConsumer(MessageProcessor processor, Duration sleepTime) {
		super(processor::process);
		this.processor = processor;
		this.sleepTime = sleepTime;
	}

	@Override
	protected void afterRunSuccess() throws InterruptedException {
		if (processor instanceof PollingMessageProcessor) {
			this.sleep();
		}
	}

	@Override
	protected void handleThrowable(Throwable throwable) {
		if (throwable instanceof RedisTimeoutException) {
			log.warn(
					"redisson stream consumer timeout, processorClass: {}, sleepTime: {}",
					processor.getClass().getName(),
					sleepTime,
					throwable
			);
			try {
				this.sleep();
			} catch (InterruptedException e) {
				this.handleInterrupt(e);
			}
			return;
		}
		super.handleThrowable(throwable);
	}

	protected void sleep() throws InterruptedException {
		ThreadUtil.sleep(sleepTime);
	}
}
