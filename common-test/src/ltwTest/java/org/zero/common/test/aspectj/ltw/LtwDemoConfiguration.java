package org.zero.common.test.aspectj.ltw;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LTW 示例配置。
 * <p>
 * 该示例依赖 AspectJ javaagent 与 {@code META-INF/aop.xml} 完成织入。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/14
 */
@Configuration(proxyBeanMethods = false)
public class LtwDemoConfiguration {
    @Bean
    LtwLoggableService ltwLoggableService() {
        return new LtwLoggableService();
    }
}
