package org.zero.common.core.extension.spring.gateway;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * Spring Gateway 的 {@linkplain org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter RouteToRequestUrlFilter} 没有扩展性可言，因此注册时替换
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
            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(MyRouteToRequestUrlFilter.class).getBeanDefinition();
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // do nothing
    }
}
