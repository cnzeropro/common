package org.zero.common.core.util.java;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetTime;
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

    @Test
    void getValue() {
        System.out.println(ClassUtil.getValue(int.class, 0));
        System.out.println(ClassUtil.getValue(double.class, new Double(0D)));
        System.out.println(ClassUtil.getValue(Byte.class, (byte) 0));
        Object offsetTime = ClassUtil.getValue(OffsetTime.class, LocalDateTime.now());
        System.out.println(offsetTime);
        Object localDateTime = ClassUtil.getValue(LocalDateTime.class, LocalDateTime.now());
        System.out.println(localDateTime);
    }
}