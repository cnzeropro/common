package org.zero.common.test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Spring AOP LTW(LoadTimeWeaving) 启用需要
 * 1、注解：{@linkplain org.springframework.context.annotation.EnableLoadTimeWeaving @EnableLoadTimeWeaving}。
 * 2、jar lib：{@code org.springframework:spring-aspects:{version}}。
 * 3、javaagent 参数 {@code -javaagent:path\spring-instrument-{version}.jar}。
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
