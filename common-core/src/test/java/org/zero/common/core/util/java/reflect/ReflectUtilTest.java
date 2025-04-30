package org.zero.common.core.util.java.reflect;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/14
 */
class ReflectUtilTest {
    @Test
    void getAllDeclaredFields() {
        Collection<Field> allDeclaredFields = FieldUtil.getAllFields(SubTestClass.class);
        System.out.println(allDeclaredFields);
    }

    @Test
    void getAllDeclaredMethods() {
        Collection<Method> allDeclaredMethods = MethodUtil.getAllMethods(SubTestClass.class);
        System.out.println(allDeclaredMethods);
    }

    @Test
    void getMethodByNameAndParam() {
        Method method = MethodUtil.getMethodByNameAndParam(SubTestClass.class, "publicSuperMethod", String.class, Integer.class);
        System.out.println(method);
    }

    @Test
    void invoke() {
        Method method = MethodUtil.getMethodByNameAndParam(SubTestClass.class, "defaultStaticSuperMethod");
        Object invoke = MethodUtil.invoke(method, ConstructorUtil.newInstance(SubTestClass.class));
        System.out.println(invoke);
    }
}