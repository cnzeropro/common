package org.zero.common.core.util.java.reflect;

import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/12
 */
class ClassLoaderUtilTest {

    @Test
    void loadClassOpt() {
        Class<?> loadedClass0 = ClassLoaderUtil.loadClassOpt("java.lang.String").orElse(null);
        System.out.println(loadedClass0);

        Class<?> loadedClass1 = ClassLoaderUtil.loadClassOpt("java/lang/reflect/Modifier").orElse(null);
        System.out.println(loadedClass1);
    }
}