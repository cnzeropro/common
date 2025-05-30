package org.zero.common.test.feature.bean.registration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 使用此注解注册的 bean，一般是配置类
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/23
 */
@Configuration
class AnnotateConfiguration {
    /**
     * 使用此注解注册的 bean，一般是三方库包的类
     */
    @Bean
    AnnotateBean annotateBean() {
        return new AnnotateBean();
    }

    static class AnnotateBean {
    }
}
