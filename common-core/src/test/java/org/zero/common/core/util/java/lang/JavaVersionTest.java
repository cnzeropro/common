package org.zero.common.core.util.java.lang;

import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/30
 */
class JavaVersionTest {
    @Test
    void getCurrent() {
        JavaVersion javaVersion = JavaVersion.getCurrent();
        System.out.println(javaVersion);
    }
}