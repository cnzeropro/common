package org.zero.common.test.config;

import org.springframework.context.annotation.Configuration;
import org.zero.common.core.aop.aspect.log.EnableTraceLog;
import org.zero.common.core.util.spring.EnableSpringUtils;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/7
 */
@EnableTraceLog
@EnableSpringUtils
@Configuration(proxyBeanMethods = false)
public class AppConfig {
}
