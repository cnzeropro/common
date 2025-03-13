package org.zero.common.core.util.java.reflect;

import lombok.SneakyThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/10
 */
public interface LambdaMeta {
    /**
     * 获取 lambda 表达式实现方法的名称
     *
     * @return lambda 表达式对应的实现方法名称
     */
    String getImplMethodName();

    /**
     * 获取 lambda 表达式实现方法的类名称
     *
     * @return lambda 表达式对应的实现方法所在类的名称
     */
    String getImplClassName();

    /**
     * 获取 lambda 表达式实现方法的类
     *
     * @return lambda 表达式对应的实现方法所在类对象
     */
    @SneakyThrows
    default Class<?> getImplClass() {
        String implClassName = this.getImplClassName();
        return ClassLoaderUtil.loadClassOpt(implClassName).orElseThrow(() -> new ClassNotFoundException(implClassName));
    }

    /**
     * 获取 lambda 表达式实现方法的实例化类名称
     *
     * @return lambda 表达式对应的实现方法实例化类的名称
     */
    String getInstantiatedClassName();

    /**
     * 获取 lambda 表达式实现方法的实例化类
     *
     * @return lambda 表达式对应的实现方法实例化类对象
     */
    @SneakyThrows
    default Class<?> getInstantiatedClass() {
        String instantiatedClassName = this.getInstantiatedClassName();
        return ClassLoaderUtil.loadClassOpt(instantiatedClassName).orElseThrow(() -> new ClassNotFoundException(instantiatedClassName));
    }
}
