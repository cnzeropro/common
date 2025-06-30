package org.zero.common.test.feature.annotation.obscure;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Spring Boot 2，需要注入 {@link io.micrometer.core.aop.TimedAspect} <br>
 * Spring Boot 3 开启此配置：{@code management.observations.annotations.enabled=true}
 *
 * @author Zero (cnzeropro@163.com)
 * @see TestConfig#timedAspect(MeterRegistry)
 * @see http://127.0.0.1/actuator/metrics/test.exec.time
 * @since 2025/5/26
 */
@RestController
@RequestMapping("/micrometer")
class TimedAnnotation {
    @Timed(value = "test.exec.time",
            description = "statistical execution time",
            histogram = true)
    @GetMapping("/test/timed")
    ResponseEntity<Void> timed() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(100, 3000));
        return ResponseEntity.ok().build();
    }
}
