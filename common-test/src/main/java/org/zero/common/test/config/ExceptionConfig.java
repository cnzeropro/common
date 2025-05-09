package org.zero.common.test.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zero.common.core.exception.MessageSourceThrowableMessageProvider;
import org.zero.common.core.exception.ThrowableMessageProvider;
import org.zero.common.core.exception.ThrowableMessageSource;
import org.zero.common.core.exception.controller.CustomErrorController;
import org.zero.common.core.exception.handler.spring.BaseExceptionHandler;
import org.zero.common.core.exception.handler.spring.JavaExceptionHandler;
import org.zero.common.core.exception.handler.spring.SpringWebExceptionHandler;
import org.zero.common.core.exception.handler.spring.SpringWebMvcExceptionHandler;

import java.util.stream.Collectors;

/**
 * 注意导入顺序，拥有子异常的处理器必须放在拥有父异常的处理器之前
 *
 * @author zero
 * @since 2024/4/11
 */
@Import({SpringWebMvcExceptionHandler.class,
        SpringWebExceptionHandler.class,
        BaseExceptionHandler.class,
        JavaExceptionHandler.class})
@EnableConfigurationProperties(ServerProperties.class)
@Configuration(proxyBeanMethods = false)
public class ExceptionConfig {
    // @Bean
    // ThrowableMessageProvider throwableMessageProvider(final JdbcTemplate jdbcTemplate) {
    //     return new JdbcTemplateThrowableMessageProvider(jdbcTemplate);
    // }

    @Bean
    ThrowableMessageProvider throwableMessageProvider() {
        return new MessageSourceThrowableMessageProvider(ThrowableMessageSource.getAccessor());
    }

    /**
     * 自定义异常控制器，用于覆盖默认的异常控制器
     *
     * @see org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration#basicErrorController(org.springframework.boot.web.servlet.error.ErrorAttributes, org.springframework.beans.factory.ObjectProvider)
     */
    @Bean
    BasicErrorController basicErrorController(ErrorAttributes errorAttributes,
                                              ObjectProvider<ErrorViewResolver> errorViewResolvers,
                                              ServerProperties serverProperties) {
        return new CustomErrorController(errorAttributes,
                serverProperties.getError(),
                errorViewResolvers.orderedStream().collect(Collectors.toList()));
    }
}
