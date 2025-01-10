package org.zero.common.test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zero.common.core.exception.handler.spring.BaseExceptionHandler;
import org.zero.common.core.exception.handler.spring.JavaExceptionHandler;
import org.zero.common.core.exception.handler.spring.SpringWebExceptionHandler;
import org.zero.common.core.exception.handler.spring.SpringWebMvcExceptionHandler;

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
@Configuration(proxyBeanMethods = false)
public class ExceptionHandlerConfig {
}
