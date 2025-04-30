package org.zero.common.core.util.java;

import org.junit.jupiter.api.Test;
import org.zero.common.core.extension.java.util.PropertiesEnhancer;

/**
 * @author zero
 * @since 2024/4/12
 */
class PropertiesEnhancerTest {
    @Test
    void test() {
        PropertiesEnhancer propertiesEnhancer = PropertiesEnhancer.init();
        propertiesEnhancer.setProperty("test", "test");
        propertiesEnhancer.setProperty("num", "1");
        Byte b = propertiesEnhancer.getBoxedByte("num");
        System.out.println(b);
    }
}