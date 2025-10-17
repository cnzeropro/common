package org.zero.common.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zero.common.core.extension.java.lang.reflect.ParameterizedTypeImpl;
import org.zero.common.core.extension.java.lang.reflect.TypeReference;
import org.zero.common.core.support.context.spring.EnableSpringUtils;
import org.zero.common.core.support.converter.ConverterComposite;
import org.zero.common.core.support.converter.ToList;
import org.zero.common.core.support.fallback.FallbackAspect;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/7
 */
@EnableSpringUtils
@Configuration(proxyBeanMethods = false)
public class AppConfig {
    @Bean
    FallbackAspect fallbackAspect() {
        return new FallbackAspect();
    }

    @PostConstruct
    public void init() {
        this.converterInit();
    }

    private void converterInit() {
        ConverterComposite converterComposite = ConverterComposite.getInstance();
        converterComposite.addConverter(new TypeReference<ArrayList<Byte>>() {
                }.getType(),
                new ToList<Byte>(ArrayList::new, Byte.class, converterComposite) {
                });
        converterComposite.addConverter(ParameterizedTypeImpl.make(ArrayList.class, Short.class),
                new ToList<Short>(ArrayList::new, Short.class, converterComposite) {
                });
        converterComposite.addConverter(ParameterizedTypeImpl.make(ArrayList.class, Integer.class),
                new ToList<Integer>(ArrayList::new, Integer.class, converterComposite) {
                });
        converterComposite.addConverter(ParameterizedTypeImpl.make(ArrayList.class, Long.class),
                new ToList<Long>(ArrayList::new, Long.class, converterComposite) {
                });
    }
}
