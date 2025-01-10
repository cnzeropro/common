package org.zero.common.core.util.spring;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.zero.common.data.exception.CommonException;

/**
 * @author zero
 * @since 2021/8/17
 */
public class SpringUtilsRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        String name = SpringUtils.class.getName();
        if (registry.isBeanNameInUse(name)) {
            throw new CommonException("SpringUtils already exists!");
        } else {
            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(SpringUtils.class).getBeanDefinition();
            registry.registerBeanDefinition(name, beanDefinition);
        }
    }
}
