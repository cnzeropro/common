package org.zero.common.test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zero.common.core.support.common.query.converter.StringToAliasArrayConverter;
import org.zero.common.core.support.common.query.converter.StringToOperatorConverter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/6
 */
@Configuration(proxyBeanMethods = false)
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToAliasArrayConverter());
        registry.addConverter(new StringToOperatorConverter());
    }
}
