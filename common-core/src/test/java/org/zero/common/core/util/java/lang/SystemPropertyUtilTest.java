package org.zero.common.core.util.java.lang;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/29
 */
class SystemPropertyUtilTest {
    @Test
    void systemProperties() {
        Properties properties = System.getProperties();
        Set<Object> keys = properties.keySet();
        int maxLen = keys.stream()
                .map(Objects::toString)
                .mapToInt(String::length)
                .max()
                .orElse(0);
        keys.stream()
                .sorted()
                .forEach(key -> {
                    System.out.print(key);
                    int keyLen = key.toString().length();
                    for (int i = 0; i < maxLen - keyLen; i++) {
                        System.out.print(" ");
                    }
                    System.out.print(" = ");
                    System.out.println(properties.get(key));
                });
    }

    @Test
    void env() {
        Map<String, String> env = System.getenv();
        Set<String> keys = env.keySet();
        int maxLen = keys.stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);
        keys.stream()
                .sorted()
                .forEach(key -> {
                    System.out.print(key);
                    int keyLen = key.length();
                    for (int i = 0; i < maxLen - keyLen; i++) {
                        System.out.print(" ");
                    }
                    System.out.print(" = ");
                    System.out.println(env.get(key));
                });
    }
}