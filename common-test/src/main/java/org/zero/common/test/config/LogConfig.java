package org.zero.common.test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.zero.common.core.aop.aspect.log.EnableTraceLog;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/7
 */
@EnableTraceLog
@EnableAspectJAutoProxy
@Configuration(proxyBeanMethods = false)
public class LogConfig {
}
