package org.zero.common.core.extension.spring.cloud.gateway.filter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter;

/**
 * 按理说可以两者都保留，但自定义的 {@linkplain CustomRouteToRequestUrlFilter} 实现已经包含 {@linkplain RouteToRequestUrlFilter} 功能，已经进行替换
 *
 * @see org.springframework.cloud.gateway.config.GatewayAutoConfiguration#routeToRequestUrlFilter()
 */
public class RouteToRequestUrlFilterBeanReplacer implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        String beanName = "routeToRequestUrlFilter";
        if (registry.containsBeanDefinition(beanName)) {
            // 替换原有实现
            registry.removeBeanDefinition(beanName);
            BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(CustomRouteToRequestUrlFilter.class).getBeanDefinition();
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // do nothing
    }
}
