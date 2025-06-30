package org.zero.common.test.feature.annotation.obscure;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Boot 2，需要注入 {@link io.micrometer.core.aop.CountedAspect} <br>
 * Spring Boot 3 开启此配置：{@code management.observations.annotations.enabled=true}
 *
 * @author Zero (cnzeropro@163.com)
 * @see TestConfig#countedAspect(MeterRegistry)
 * @see http://127.0.0.1/actuator/metrics/test.exec.count
 * @since 2025/5/26
 */
@RestController
@RequestMapping("/micrometer")
class CountedAnnotation {
    @Counted(value = "test.exec.count",
            description = "statistical execution count")
    @GetMapping("/test/counted")
    ResponseEntity<Void> counted() {
        return ResponseEntity.ok().build();
    }
}
