package org.zero.common.core.util.java;

import org.junit.jupiter.api.Test;
import org.zero.common.core.util.java.lang.ClassUtil;

import java.util.Collection;
import java.util.Map;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2021/9/8 16:54
 */
class ClassUtilTest {
    @Test
    void getClassMap() {
        Map<String, Class<?>> classMap = ClassUtil.getClassMap("org.zero.common.data.model");
        System.out.println(classMap);
    }

    @Test
    void getClassNames() {
        Collection<String> classNames = ClassUtil.getClassNames("org.zero.common");
        classNames.forEach(System.out::println);
    }
}