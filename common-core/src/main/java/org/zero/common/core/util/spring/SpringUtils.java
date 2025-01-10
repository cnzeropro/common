package org.zero.common.core.util.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.zero.common.data.exception.UtilException;

import java.util.Objects;
import java.util.Optional;

/**
 * Spring 工具类，包括：IOC 容器获取、注册、注销 bean；获取环境配置等。
 *
 * <p>
 * 通过注解使用注册器方式注入该类，参见：{@link EnableSpringUtils}
 * <p>
 * 为什么不使用 @Component 直接注入呢？因为考虑到三方引用可能并没有该包的 ComponentScan
 *
 * @author Zero (cnzeropro@qq.com)
 */
public class SpringUtils implements BeanFactoryPostProcessor, ApplicationContextAware {
    public static Object getBean(String name) {
        return getBeanFactory().getBean(name);
    }

    public static Object getBean(String name, Object... args) {
        return getBeanFactory().getBean(name, args);
    }

    public static <T> T getBean(Class<T> type) {
        return getBeanFactory().getBean(type);
    }

    public static <T> T getBean(String name, Class<T> type) {
        return getBeanFactory().getBean(name, type);
    }

    public static <T> T getBean(Class<T> type, Object... args) {
        return getBeanFactory().getBean(type, args);
    }

    /**
     * 获取指定 Bean 的 Provider
     * <p>
     * Spring 5.1+ 支持获取 Bean Provider
     */
    public static <T> ObjectProvider<T> getBeanProvider(Class<T> type) {
        return getBeanFactoryOpt().map(bf -> bf.getBeanProvider(type)).orElseGet(EmptyObjectProvider::new);
    }

    /**
     * 获取指定 Bean 的 Provider
     * <p>
     * Spring 5.1+ 支持获取 Bean Provider
     */
    public static <T> ObjectProvider<T> getBeanProvider(ResolvableType type) {
        return getBeanFactoryOpt().map(bf -> bf.<T>getBeanProvider(type)).orElseGet(EmptyObjectProvider::new);
    }

    /**
     * 获取指定 Bean 的 Provider
     * <p>
     * Spring 5.3+ 才支持 allowEagerInit 参数
     */
    public static <T> ObjectProvider<T> getBeanProvider(Class<T> type, boolean allowEagerInit) {
        return getApplicationContextOpt().map(ac -> ac.getBeanProvider(type, allowEagerInit)).orElseGet(EmptyObjectProvider::new);
    }

    /**
     * 获取指定 Bean 的 Provider
     * <p>
     * Spring 5.3+ 才支持 allowEagerInit 参数
     */
    public static <T> ObjectProvider<T> getBeanProvider(ResolvableType type, boolean allowEagerInit) {
        return getApplicationContextOpt().map(ac -> ac.<T>getBeanProvider(type, allowEagerInit)).orElseGet(EmptyObjectProvider::new);
    }

    /**
     * 动态向 Spring 容器注册 Bean
     */
    public static <T> void registerBean(String beanName, T bean) {
        final BeanFactory beanFactory = getBeanFactory();
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            ConfigurableListableBeanFactory factory = (ConfigurableListableBeanFactory) beanFactory;
            factory.autowireBean(bean);
            factory.registerSingleton(beanName, bean);
            return;
        }
        final ApplicationContext applicationContext = getApplicationContext();
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
            ConfigurableListableBeanFactory factory = context.getBeanFactory();
            factory.autowireBean(bean);
            factory.registerSingleton(beanName, bean);
            return;
        }
        throw new UtilException("Can not register bean, the factory or context not supported");
    }

    /**
     * 动态从 Spring 容器注销 Bean
     * <p>
     * 注意：请谨慎使用
     */
    public static void unregisterBean(String beanName) {
        final BeanFactory beanFactory = getBeanFactory();
        if (beanFactory instanceof DefaultSingletonBeanRegistry) {
            DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) beanFactory;
            registry.destroySingleton(beanName);
            return;
        }
        throw new UtilException("Can not unregister bean, the factory or context not supported");
    }

    /**
     * 获取属性值
     */
    public static String getProperty(String key) {
        return getProperty(key, (String) null);
    }

    /**
     * 获取属性值
     */
    public static String getProperty(String key, String defaultValue) {
        return getEnvironmentOpt().map(env -> env.getProperty(key)).orElse(defaultValue);
    }

    /**
     * 获取属性值
     */
    public static <T> T getProperty(String key, Class<T> type) {
        return getProperty(key, type, null);
    }

    /**
     * 获取属性值
     */
    public static <T> T getProperty(String key, Class<T> type, T defaultValue) {
        return getEnvironmentOpt().map(env -> env.getProperty(key, type)).orElse(defaultValue);
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName() {
        return getProperty("spring.application.name");
    }

    /**
     * 解析占位符
     */
    public static String resolvePlaceholders(String text) {
        return getEnvironmentOpt().map(env -> env.resolvePlaceholders(text)).orElse(text);
    }

    /**
     * 获取当前配置环境，无配置返回空数组
     */
    public static String[] getActiveProfiles() {
        return getEnvironmentOpt().map(Environment::getActiveProfiles).orElse(new String[0]);
    }

    /**
     * 获取当前配置环境，默认取第一个
     */
    public static String getActiveProfile() {
        final String[] activeProfiles = getActiveProfiles();
        return activeProfiles.length > 0 ? activeProfiles[0] : null;
    }

    /**
     * 发布事件
     */
    public static void publishEvent(ApplicationEvent event) {
        getApplicationContext().publishEvent(event);
    }

    /**
     * 发布事件
     * <p>
     * Spring 4.2+ 事件可以不再是{@link ApplicationEvent}的子类
     */
    public static void publishEvent(Object event) {
        getApplicationContext().publishEvent(event);
    }

    /* ******************************************* Context Getter ******************************************* */

    /**
     * 获取 {@link BeanFactory}
     */
    public static BeanFactory getBeanFactory() {
        return getBeanFactoryOpt().orElseThrow(() -> new UtilException("BeanFactory is null"));
    }

    public static Optional<BeanFactory> getBeanFactoryOpt() {
        return Objects.nonNull(beanFactory) ? Optional.of(beanFactory) : Optional.ofNullable(applicationContext);
    }

    /**
     * 获取 {@link ConfigurableListableBeanFactory}
     */
    public static ConfigurableListableBeanFactory getConfigurableListableBeanFactory() {
        return getConfigurableListableBeanFactoryOpt().orElseThrow(() -> new UtilException("ConfigurableListableBeanFactory is null"));
    }

    public static Optional<ConfigurableListableBeanFactory> getConfigurableListableBeanFactoryOpt() {
        return Optional.ofNullable(beanFactory);
    }

    /**
     * 获取 {@link ApplicationContext}
     */
    public static ApplicationContext getApplicationContext() {
        return getApplicationContextOpt().orElseThrow(() -> new UtilException("ApplicationContext is null"));
    }

    public static Optional<ApplicationContext> getApplicationContextOpt() {
        return Optional.ofNullable(applicationContext);
    }

    /**
     * 获取 {@link Environment}
     */
    public static Environment getEnvironment() {
        return getEnvironmentOpt().orElseThrow(() -> new UtilException("No Environment from spring context"));
    }

    public static Optional<Environment> getEnvironmentOpt() {
        return getApplicationContextOpt().map(ApplicationContext::getEnvironment);
    }

    /* ******************************************* Context Setter ******************************************* */

    private static ConfigurableListableBeanFactory beanFactory;

    protected static void setCLBF(ConfigurableListableBeanFactory bf) {
        beanFactory = bf;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        setCLBF(beanFactory);
    }

    private static ApplicationContext applicationContext;

    protected static void setAC(ApplicationContext ac) {
        applicationContext = ac;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setAC(applicationContext);
    }
}
