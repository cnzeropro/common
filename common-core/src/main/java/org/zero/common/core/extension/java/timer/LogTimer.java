package org.zero.common.core.extension.java.timer;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/23
 */
@Slf4j
public final class LogTimer extends BaseTimer<LogTimer> {
    public static LogTimer start() {
        return new LogTimer();
    }

    public static LogTimer start(String name) {
        return new LogTimer(name);
    }

    public LogTimer log() {
        return this.consume((name, duration) -> log.info("{} time consumption: {}", name, duration));
    }

    @Override
    public void close()  {
        this.log().reset();
    }

    private LogTimer() {
        super();
    }

    private LogTimer(String name) {
        super(name);
    }
}
