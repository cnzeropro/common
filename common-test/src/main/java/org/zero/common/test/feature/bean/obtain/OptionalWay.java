package org.zero.common.test.feature.bean.obtain;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/26
 */
@Component
class OptionalWay {
    final Environment environment;

    OptionalWay(Optional<Environment> environmentOptional) {
        this.environment = environmentOptional.orElse(null);
    }
}
