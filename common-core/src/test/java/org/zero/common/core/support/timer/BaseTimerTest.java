package org.zero.common.core.support.timer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/29
 */
@Slf4j
class BaseTimerTest {
    @Test
    void printTimer() {
        try (PrintTimer timer = PrintTimer.start()) {
           // do something
        }
    }

    @Test
    void logTimer() {
        try (LogTimer timer = LogTimer.start(log)) {
            // do something
        }
    }
}