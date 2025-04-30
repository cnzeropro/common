package org.zero.common.test.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zero.common.core.extension.jackson.JavaTimeJackson2ObjectMapperBuilderCustomizer;
import org.zero.common.core.extension.jackson.JsonJavaTimeProperties;
import org.zero.common.core.extension.jackson.databind.NumberJsonComponent;
import org.zero.common.core.extension.jackson.databind.ser.JSONNullSerializer;
import org.zero.common.data.exception.BaseSysError;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/8
 */
@Import({JSONNullSerializer.class, NumberJsonComponent.class})
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
    Jackson2ObjectMapperBuilderCustomizer javaTimeJackson2ObjectMapperBuilderCustomizer() {
        return new JavaTimeJackson2ObjectMapperBuilderCustomizer(jsonJavaTimeProperties);
    }

    @Bean
    Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.mixIn(BaseSysError.class, BaseSysErrorMixIn.class);
    }

    private interface BaseSysErrorMixIn{
        @JsonIgnore
        boolean isOk();
    }
}
