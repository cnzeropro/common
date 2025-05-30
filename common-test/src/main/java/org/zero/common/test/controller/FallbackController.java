package org.zero.common.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zero.common.core.support.fallback.Fallback;
import org.zero.common.data.model.view.Result;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/28
 */
@Slf4j
@RestController
@RequestMapping("fallback")
public class FallbackController {
    @Fallback("f1Fallback")
    @GetMapping("f1")
    public Result<LocalDateTime> f1(String a, Integer b) {
        int i = ThreadLocalRandom.current().nextInt(100);
        if (i < 49) {
            throw new NullPointerException();
        }
        if (i < 89) {
            throw new RuntimeException();
        }
        return Result.ok(LocalDateTime.now());
    }

    // private Result<LocalDateTime> f1Fallback(String a, Integer b, RuntimeException runtimeException) {
    //     return Result.error("runtimeException");
    // }
    //
    // private Result<LocalDateTime> f1Fallback(String a, Integer b, NullPointerException nullPointerException) {
    //     return Result.error("nullPointerException");
    // }

    private Result<LocalDateTime> f1Fallback(String a, Integer b, Exception exception) {
        return Result.error("exception");
    }

    private Result<LocalDateTime> f1Fallback(String a, Integer b, Throwable throwable) {
        return Result.error("throwable");
    }

    private Result<LocalDateTime> f1Fallback(String a, Integer b) {
        return Result.error(a + b);
    }

    private Result<LocalDateTime> f1Fallback(String a, int b) {
        return Result.error(a + b);
    }

    private Result<LocalDateTime> f1Fallback(String a, Number b) {
        return Result.error(a + b);
    }

    private Result<LocalDateTime> f1Fallback(CharSequence a) {
        return Result.error(a);
    }
}
