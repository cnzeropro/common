package org.zero.common.test.feature.bean.obtain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/26
 */
@Component
class ValueAnnotation {
    @Value("#{@environment}")
    Environment environment;
}
