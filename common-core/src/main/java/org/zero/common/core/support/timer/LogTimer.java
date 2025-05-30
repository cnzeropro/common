package org.zero.common.core.support.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/23
 */
public class LogTimer extends BaseTimer<LogTimer> {
    protected Logger logger = LoggerFactory.getLogger(LogTimer.class);
    protected Level level = Level.INFO;

    public static LogTimer start() {
        return new LogTimer();
    }

    public static LogTimer start(Logger logger) {
        return new LogTimer(logger);
    }

    public static LogTimer start(Level level) {
        return new LogTimer(level);
    }

    public static LogTimer start(Logger logger, Level level) {
        return new LogTimer(logger, level);
    }

    public static LogTimer start(String name) {
        return new LogTimer(name);
    }

    public static LogTimer start(String name, Logger logger) {
        return new LogTimer(name, logger);
    }

    public static LogTimer start(String name, Level level) {
        return new LogTimer(name, level);
    }

    public static LogTimer start(String name, Logger logger, Level level) {
        return new LogTimer(name, logger, level);
    }

    public LogTimer log() {
        return this.consume((name, duration) -> this.log("{} time consumption: {}", name, duration));
    }

    protected void log(String format, Object... args) {
        switch (level) {
            case TRACE:
                logger.trace(format, args);
                break;
            case DEBUG:
                logger.debug(format, args);
                break;
            case INFO:
                logger.info(format, args);
                break;
            case WARN:
                logger.warn(format, args);
                break;
            case ERROR:
                logger.error(format, args);
                break;
            default:
        }
    }


    @Override
    public void close() {
        this.log().reset();
    }

    protected LogTimer() {
        super();
    }

    protected LogTimer(Logger logger) {
        super();
        this.logger = logger;
    }

    protected LogTimer(Level level) {
        super();
        this.level = level;
    }

    protected LogTimer(Logger logger, Level level) {
        super();
        this.logger = logger;
        this.level = level;
    }

    protected LogTimer(String name) {
        super(name);
    }

    protected LogTimer(String name, Logger logger) {
        super(name);
        this.logger = logger;
    }

    protected LogTimer(String name, Level level) {
        super(name);
        this.level = level;
    }

    protected LogTimer(String name, Logger logger, Level level) {
        super(name);
        this.logger = logger;
        this.level = level;
    }
}
