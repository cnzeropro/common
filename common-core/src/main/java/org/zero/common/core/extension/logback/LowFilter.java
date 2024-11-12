package org.zero.common.core.extension.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

import java.util.Objects;

/**
 * 低等级过滤器：过滤出小于等于指定级别的日志，作用与{@link ch.qos.logback.classic.filter.ThresholdFilter}相反
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

        if (event.getLevel().levelInt <= level.levelInt) {
            return FilterReply.NEUTRAL;
        } else {
            return FilterReply.DENY;
        }
    }

    @Override
    public void start() {
        if (Objects.nonNull(this.level)) {
            super.start();
        }
    }

    public void setLevel(String level) {
        this.level = Level.toLevel(level);
    }
}
