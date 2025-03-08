package org.zero.common.test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Spring AOP LTW (LoadTimeWeaving) 启用需要
 * <ul>
 *     <li>注解：{@linkplain org.springframework.context.annotation.EnableLoadTimeWeaving @EnableLoadTimeWeaving}</li>
 *     <li>jar lib：{@code org.springframework:spring-aspects:{version}}</li>
 *     <li>javaagent 参数 {@code -javaagent:path\spring-instrument-{version}.jar}</li>
 * </ul>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/21
 */
// @EnableSpringConfigured
// @EnableLoadTimeWeaving
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Configuration(proxyBeanMethods = false)
public class AopConfig {
}
