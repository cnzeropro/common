package org.zero.common.test;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/22
 */
public class BeanRegistration {
    /**
     * 使用此注解注册的 bean，一般是自己项目的普通类
     */
    @Component
    static class A {
    }

    /**
     * 使用此注解注册的 bean，一般是数据访问层的类（DAO）
     *
     * @see org.apache.ibatis.annotations.Mapper
     */
    @Repository
    static class B {
    }

    /**
     * 使用此注解注册的 bean，一般是服务层的类
     */
    @Service
    static class C {
    }

    /**
     * 使用此注解注册的 bean，一般是视图层的类
     *
     * @see org.springframework.web.bind.annotation.RestController
     */
    @Controller
    static class D {
    }

    /**
     * 使用此注解注册的 bean，一般是配置类
     */
    @Configuration
    static class E {
        /**
         * 使用此注解注册的 bean，一般是三方库包的类
         */
        @Bean
        F f() {
            return new F();
        }

        static class F {
        }
    }

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
    @Import({H.class, IImportSelector.class, JImportBeanDefinitionRegistrar.class})
    @Configuration(proxyBeanMethods = false)
    static class G {
    }

    static class H {
    }

    static class IImportSelector implements ImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{I.class.getName()};
        }

        static class I {
        }
    }

    static class JImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(J.class).getBeanDefinition();
            registry.registerBeanDefinition("j", beanDefinition);
        }

        static class J {
        }
    }
}
