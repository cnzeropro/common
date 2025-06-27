package org.zero.common.test.feature.bean.obtain;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/26
 */
@Component
class ResourceAnnotation {
    @Resource
    Environment environment;
}
