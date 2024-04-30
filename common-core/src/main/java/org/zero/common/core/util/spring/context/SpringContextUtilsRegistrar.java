package org.zero.common.core.util.spring.context;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author zero
 * @since 2021/8/17
 */
public class SpringContextUtilsRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        String name = SpringContextUtils.class.getName();
        if (!registry.isBeanNameInUse(name)) {
            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(SpringContextUtils.class).getBeanDefinition();
            registry.registerBeanDefinition(name, beanDefinition);
        }
    }
}
