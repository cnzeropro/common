package org.zero.common.core.util.spring.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.zero.common.data.exception.UtilException;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 通过注解使用注册器方式注入该类
 * <p>
 * 为什么不使用@Component直接注入呢？因为考虑到三方引用可能并没有该包的ComponentScan
 *
 * @author Zero (cnzeropro@qq.com)
 */
// @Component
public class SpringContextUtils implements BeanFactoryPostProcessor, ApplicationContextAware {
    /**
     * Spring 可配置的Bean工厂
     * <p>
     * "@PostConstruct"注解标记的类中，由于ApplicationContext还未加载，会导致NPE，因此实现BeanFactoryPostProcessor注入ConfigurableListableBeanFactory实现bean的操作
     */
    private static ConfigurableListableBeanFactory beanFactory;
    /**
     * Spring 上下文对象实例
     */
    private static ApplicationContext applicationContext;

    public static Optional<ApplicationContext> getApplicationContextOpt() {
        return Optional.ofNullable(applicationContext);
    }

    /**
     * 获取 {@link ApplicationContext}
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取 {@link ListableBeanFactory}
     */
    public static ListableBeanFactory getBeanFactory() {
        return Objects.isNull(beanFactory) ? applicationContext : beanFactory;
    }

    /**
     * 获取 {@link ConfigurableListableBeanFactory}
     */
    public static ConfigurableListableBeanFactory getConfigurableBeanFactory() {
        if (Objects.nonNull(beanFactory)) {
            return beanFactory;
        } else if (applicationContext instanceof ConfigurableApplicationContext) {
            return ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        }
        throw new UtilException("No ConfigurableListableBeanFactory from context");
    }

    public static Object getBean(String name) {
        return getBeanFactory().getBean(name);
    }

    public static <T> T getBean(Class<T> type) {
        return getBeanFactory().getBean(type);
    }

    public static <T> T getBean(String name, Class<T> type) {
        return getBeanFactory().getBean(name, type);
    }

    /**
     * 获取带泛型参数的Bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(ParameterizedTypeReference<T> reference) {
        Type type = reference.getType();
        ResolvableType resolvableType = ResolvableType.forType(type);
        Class<T> rawClass = (Class<T>) resolvableType.getRawClass();
        ResolvableType[] generics = resolvableType.getGenerics();

        if (Objects.isNull(rawClass)) {
            return getBean((Class<T>) type);
        }

        final String[] beanNames = getBeanNamesForType(ResolvableType.forClassWithGenerics(rawClass, generics));
        return getBean(beanNames[0], rawClass);
    }

    /**
     * 获取指定类型对应的所有Bean，包括子类
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return getBeanFactory().getBeansOfType(type);
    }

    /**
     * 获取指定Bean的Provider
     */
    public static <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType, boolean allowEagerInit) {
        return getBeanFactory().getBeanProvider(requiredType, allowEagerInit);
    }

    /**
     * 获取指定Bean的Provider
     */
    public static <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        return getBeanFactory().getBeanProvider(requiredType);
    }

    /**
     * 获取指定类型对应的Bean名称，包括子类
     */
    public static String[] getBeanNamesForType(Class<?> type) {
        return getBeanFactory().getBeanNamesForType(type);
    }

    /**
     * 获取指定类型对应的Bean名称，包括子类
     */
    public static String[] getBeanNamesForType(ResolvableType type) {
        return getBeanFactory().getBeanNamesForType(type);
    }

    /**
     * 获取应用环境 Opt
     */
    public static Optional<Environment> getEnvironmentOpt() {
        return getApplicationContextOpt().map(ApplicationContext::getEnvironment);
    }

    /**
     * 获取应用环境
     */
    public static Environment getEnvironment() {
        return getEnvironmentOpt().orElse(null);
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
     * 解析占位符
     */
    public static String resolvePlaceholders(String text) {
        return getEnvironmentOpt().map(env -> env.resolvePlaceholders(text)).orElse(null);
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName() {
        return getProperty("spring.application.name");
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
        getApplicationContextOpt().ifPresent(applicationContext -> applicationContext.publishEvent(event));
    }

    /**
     * 发布事件
     */
    public static void publishEvent(Object event) {
        getApplicationContextOpt().ifPresent(applicationContext -> applicationContext.publishEvent(event));
    }

    /**
     * 动态向Spring注册Bean
     */
    public static <T> void registerBean(String beanName, T bean) {
        final ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
        factory.autowireBean(bean);
        factory.registerSingleton(beanName, bean);
    }

    /**
     * 注销Bean
     * <p>
     * 注意：请谨慎使用
     */
    public static void unregisterBean(String beanName) {
        final ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
        if (factory instanceof DefaultSingletonBeanRegistry) {
            DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) factory;
            registry.destroySingleton(beanName);
        } else {
            throw new UtilException("Can not unregister bean, the factory is not a DefaultSingletonBeanRegistry!");
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        setBf(beanFactory);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setAc(applicationContext);
    }

    /**
     * Sonar 实例方法不应写入“静态”字段 修复
     */
    private static void setBf(ConfigurableListableBeanFactory bf) {
        SpringContextUtils.beanFactory = bf;
    }

    /**
     * Sonar 实例方法不应写入“静态”字段 修复
     */
    private static void setAc(ApplicationContext ac) {
        applicationContext = ac;
    }
}
