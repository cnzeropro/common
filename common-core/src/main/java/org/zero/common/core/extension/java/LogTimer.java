package org.zero.common.core.extension.java;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/23
 */
@Slf4j
public class LogTimer extends BaseTimer<LogTimer> {
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

    protected LogTimer() {
        super();
    }

    protected LogTimer(String name) {
        super(name);
    }
}
