package org.zero.common.test.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;
import org.zero.common.core.extension.common.query.converter.StringArrayToFieldArrayConverter;
import org.zero.common.core.extension.common.query.converter.StringToFieldArrayConverter;
import org.zero.common.core.extension.common.query.converter.StringToFieldConverter;
import org.zero.common.core.extension.common.query.converter.StringToOperatorConverter;
import org.zero.common.core.extension.spring.web.method.support.JsonObjectArgumentResolvers;
import org.zero.common.core.support.bean.dynamic.DynamicBeanArgumentResolver;

import java.util.List;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/6
 */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class WebMvcConfig implements WebMvcConfigurer {
    private final ObjectMapper objectMapper;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToFieldConverter());
        registry.addConverter(new StringToFieldArrayConverter());
        registry.addConverter(new StringArrayToFieldArrayConverter());
        registry.addConverter(new StringToOperatorConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new DynamicBeanArgumentResolver(true));
        resolvers.add(new JsonObjectArgumentResolvers.JacksonArgumentResolver(objectMapper));
        resolvers.add(new JsonObjectArgumentResolvers.HutoolJsonArgumentResolver());
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        // 启用矩阵变量
        urlPathHelper.setRemoveSemicolonContent(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }
}
