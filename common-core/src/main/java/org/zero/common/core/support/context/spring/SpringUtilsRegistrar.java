package org.zero.common.core.support.context.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author zero
 * @since 2021/8/17
 */
@Slf4j
public class SpringUtilsRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        String name = SpringUtils.class.getCanonicalName();
        if (registry.isBeanNameInUse(name)) {
            log.warn("SpringUtils already exists!");
        } else {
            BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(SpringUtils.class).getBeanDefinition();
            registry.registerBeanDefinition(name, beanDefinition);
        }
    }
}
