package org.zero.common.core.util.java;

import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2021/9/8 16:54
 */
class ClassUtilTest {
    @Test
    void getClasses() {
        List<Class<?>> classes = ClassUtil.getClasses("org.zero.common.data.model");
        classes.forEach(System.out::println);
    }

    @Test
    void getClassNames() {
        List<String> classNames = ClassUtil.getClassNames("org.zero.common.data");
        classNames.forEach(System.out::println);
    }
}