package org.zero.common.test.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zero.common.core.extension.jackson.JSONNullJsonComponent;
import org.zero.common.core.extension.jackson.JavaTimeJackson2ObjectMapperBuilderCustomizer;
import org.zero.common.core.extension.jackson.JsonJavaTimeProperties;
import org.zero.common.core.extension.jackson.NumberJsonComponent;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/8
 */
@Import({JSONNullJsonComponent.class, NumberJsonComponent.class})
@EnableConfigurationProperties(JsonJavaTimeProperties.class)
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class JacksonConfig {
    private final JsonJavaTimeProperties jsonJavaTimeProperties;

    /**
     * 配置 Jackson
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return new JavaTimeJackson2ObjectMapperBuilderCustomizer(jsonJavaTimeProperties);
    }
}
