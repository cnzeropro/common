package org.zero.common.core.util.java.reflect;

import org.junit.jupiter.api.Test;
import org.zero.common.core.util.java.lang.reflect.ConstructorUtil;
import org.zero.common.core.util.java.lang.reflect.MethodUtil;

import java.lang.reflect.Method;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/14
 */
class ReflectUtilTest {

    @Test
    void getMethodByNameAndParam() {
        Method method = MethodUtil.getByNameAndParam(SubTestClass.class, true,"publicSuperMethod", String.class, Integer.class);
        System.out.println(method);
    }

    @Test
    void invoke() {
        Method method = MethodUtil.getByNameAndParam(SubTestClass.class, true,"defaultStaticSuperMethod");
        Object invoke = MethodUtil.invoke(method, ConstructorUtil.newInstance(SubTestClass.class));
        System.out.println(invoke);
    }
}