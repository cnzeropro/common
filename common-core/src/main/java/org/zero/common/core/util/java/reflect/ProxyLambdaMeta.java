package org.zero.common.core.util.java.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Executable;
import java.lang.reflect.Proxy;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/10
 */
public class ProxyLambdaMeta implements LambdaMeta {
    private final Executable executable;

    public ProxyLambdaMeta(Proxy proxy) {
        MethodHandle methodHandle = MethodHandleProxies.wrapperInstanceTarget(proxy);
        this.executable = MethodHandles.reflectAs(Executable.class, methodHandle);
    }

    @Override
    public String getImplMethodName() {
        return executable.getName();
    }

    @Override
    public String getImplClassName() {
        return this.getImplClass().getName();
    }

    @Override
    public Class<?> getImplClass() {
        return executable.getDeclaringClass();
    }

    @Override
    public String getInstantiatedClassName() {
        return this.getImplClassName();
    }

    @Override
    public Class<?> getInstantiatedClass() {
        return this.getImplClass();
    }

    @Override
    public String toString() {
        return String.format("%s::%s", this.getInstantiatedClassName(), this.getImplMethodName());
    }
}
