package org.zero.common.core.util.java.lang.reflect;

import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;
import org.zero.common.data.exception.UtilException;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.logging.Level;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/10
 */
@Log
@UtilityClass
public class LambdaUtil {
    public static LambdaMeta extract(Object lambda) {
        // 1. IDEA 调试模式下 lambda 表达式是一个代理
        if (lambda instanceof Proxy) {
            return new ProxyLambdaMeta((Proxy) lambda);
        }
        if (!isLambda(lambda)) {
            throw new UtilException(String.format("This is not a lambda expression: %s", lambda));
        }
        if (!(lambda instanceof Serializable)) {
            throw new UtilException("Lambda expression is not serializable");
        }
        // 2. 反射读取
        try {
            Class<?> lambdaClass = lambda.getClass();
            Method method = lambdaClass.getDeclaredMethod("writeReplace");
            Object invoked = MethodUtil.invoke(method, lambda);
            return new ReflectLambdaMeta((java.lang.invoke.SerializedLambda) invoked, lambdaClass.getClassLoader());
        } catch (Throwable e) {
            log.log(Level.FINE, "Failed to extract lambda meta with reflect", e);
            // 3. 反射失败使用序列化的方式读取
            return new ShadowLambdaMeta((Serializable) lambda);
        }
    }

    public static boolean isLambda(Object obj) {
        if (Objects.isNull(obj)) {
            return false;
        }
        Class<?> clazz = obj.getClass();
        // 必定是合成类（Synthetic）
        return clazz.isSynthetic() &&
                // 存在固定标识符：$$Lambda
                clazz.getSimpleName().contains("$$Lambda") &&
                // 至少实现一个接口
                clazz.getInterfaces().length > 0 &&
                // 不是代理类
                !Proxy.isProxyClass(clazz) &&
                // 不是匿名类
                !clazz.isAnonymousClass();
    }
}
