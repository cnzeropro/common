package org.zero.common.core.extension.spring.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

import java.util.UUID;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/26
 */
public class CustomBeanNameGenerator implements BeanNameGenerator {
	@Override
	public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
		String beanName = AnnotationBeanNameGenerator.INSTANCE.generateBeanName(definition, registry);
		if (!registry.isBeanNameInUse(beanName)) {
			return beanName;
		}
		beanName = FullyQualifiedAnnotationBeanNameGenerator.INSTANCE.generateBeanName(definition, registry);
		if (!registry.isBeanNameInUse(beanName)) {
			return beanName;
		}
		beanName = DefaultBeanNameGenerator.INSTANCE.generateBeanName(definition, registry);
		if (!registry.isBeanNameInUse(beanName)) {
			return beanName;
		}
		return String.format("%s-%s", beanName, UUID.randomUUID());
	}
}
