package org.zero.common.core.extension.logback.core.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * 低等级日志过滤器。
 * <p>
 * 配置 {@code level} 后，放行小于等于该级别的日志事件，拒绝高于该级别的日志事件。常用于和
 * {@link ch.qos.logback.classic.filter.ThresholdFilter} 组合，将不同级别范围的日志分流到不同 appender。
 * <p>
 * 配置缺失或无法识别时过滤器不会启动，避免 Logback 将非法级别静默回退为 {@code DEBUG}。
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2021/9/6
 */
public class LowFilter extends Filter<ILoggingEvent> {
	private Level level;

	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (!isStarted()) {
			return FilterReply.NEUTRAL;
		}

		// level 是允许通过的最高级别，边界级别本身也会放行。
		if (level.isGreaterOrEqual(event.getLevel())) {
			return FilterReply.NEUTRAL;
		} else {
			return FilterReply.DENY;
		}
	}

	/**
	 * 启动过滤器。
	 * <p>
	 * 只有在日志级别配置有效时才进入启动状态，否则保持中立行为并记录配置错误。
	 */
	@Override
	public void start() {
		if (this.level != null) {
			super.start();
		} else {
			addError("No valid level is configured for " + getClass().getName() + ".");
		}
	}

	/**
	 * 设置允许通过的最高日志级别。
	 *
	 * @param level Logback 日志级别名称，如 {@code TRACE}、{@code DEBUG}、{@code INFO}
	 */
	public void setLevel(String level) {
		this.level = Level.toLevel(level, null);
	}
}
