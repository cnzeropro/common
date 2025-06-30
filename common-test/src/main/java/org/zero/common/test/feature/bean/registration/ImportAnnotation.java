package org.zero.common.test.feature.bean.registration;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 使用此注解注册的 bean，可以受到不同程度的控制。
 * <table>
 *     <tr>
 *         <th>类型</th>
 *         <th>Bean Name (ID)</th>
 *         <th>适用场景</th>
 *     </tr>
 *     <tr>
 *         <td>普通类</td>
 *         <td>固定（全限定名）</td>
 *         <td>简单 Bean 注册，无需动态逻辑，灵活性很低</td>
 *     </tr>
 *     <tr>
 *         <td>{@link ImportSelector}</td>
 *         <td>固定（全限定名）</td>
 *         <td>动态选择 Bean 类（如环境配置），灵活性适中</td>
 *     </tr>
 *     <tr>
 *         <td>{@link ImportBeanDefinitionRegistrar}</td>
 *         <td>可自定义</td>
 *         <td>需要精细控制 Bean 定义的场景，灵活性较高</td>
 *     </tr>
 * </table>
 */
@Import({ImportAnnotation.Bean.class, ImportAnnotation.BeanImportSelector.class, ImportAnnotation.BeanImportBeanDefinitionRegistrar.class})
@Configuration(proxyBeanMethods = false)
class ImportAnnotation {

    static class Bean {
    }

    static class BeanImportSelector implements ImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{Bean.class.getName()};
        }

        static class Bean {
        }
    }

    static class BeanImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(Bean.class).getBeanDefinition();
            registry.registerBeanDefinition("bean", beanDefinition);
        }

        static class Bean {
        }
    }
}
