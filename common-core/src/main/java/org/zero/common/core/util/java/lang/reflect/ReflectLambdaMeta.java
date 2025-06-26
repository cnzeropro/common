package org.zero.common.core.util.java.lang.reflect;

import lombok.SneakyThrows;
import org.zero.common.core.util.java.lang.ClassLoaderUtil;

import java.lang.invoke.SerializedLambda;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/10
 */
public class ReflectLambdaMeta implements LambdaMeta {
    private final SerializedLambda lambda;
    private final ClassLoader classLoader;

    public ReflectLambdaMeta(SerializedLambda lambda) {
        this(lambda, lambda.getClass().getClassLoader());
    }

    public ReflectLambdaMeta(SerializedLambda lambda, ClassLoader classLoader) {
        this.lambda = lambda;
        this.classLoader = classLoader;
    }

    @Override
    public String getImplMethodName() {
        return lambda.getImplMethodName();
    }

    @Override
    public String getImplClassName() {
        return lambda.getImplClass().replace("/", ".");
    }

    @SneakyThrows
    @Override
    public Class<?> getImplClass() {
        String implClassName = this.getImplClassName();
        return ClassLoaderUtil.loadClassOpt(implClassName, classLoader).orElseThrow(() -> new ClassNotFoundException(implClassName));
    }

    @Override
    public String getInstantiatedClassName() {
        String instantiatedMethodType = lambda.getInstantiatedMethodType();
        String instantiatedType = instantiatedMethodType.substring(2, instantiatedMethodType.indexOf(";"));
        return instantiatedType.replace("/", ".");
    }

    @SneakyThrows
    @Override
    public Class<?> getInstantiatedClass() {
        String instantiatedClassName = this.getInstantiatedClassName();
        return ClassLoaderUtil.loadClassOpt(instantiatedClassName, classLoader).orElseThrow(() -> new ClassNotFoundException(instantiatedClassName));
    }

    @Override
    public String toString() {
        return String.format("%s::%s", this.getInstantiatedClassName(), this.getImplMethodName());
    }
}
