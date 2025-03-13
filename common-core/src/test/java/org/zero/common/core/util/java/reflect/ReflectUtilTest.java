package org.zero.common.core.util.java.reflect;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/14
 */
class ReflectUtilTest {
    @Test
    void getAllDeclaredFields() {
        List<Field> allDeclaredFields = FieldUtil.getAllDeclaredFields(SubTestClass.class);
        System.out.println(allDeclaredFields);
    }

    @Test
    void getAllDeclaredMethods() {
        List<Method> allDeclaredMethods = MethodUtil.getAllDeclaredMethods(SubTestClass.class);
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