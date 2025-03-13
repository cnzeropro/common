package org.zero.common.core.util.java.map;

import lombok.experimental.UtilityClass;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/10
 */
@UtilityClass
public class MapUtil {
    /**
     * JDK 8 {@linkplain Map#computeIfAbsent(Object, Function) computeIfAbsent} 方法性能问题修复
     *
     * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8161372">JDK-8161372</a>
     */
    public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<K, V> mappingFunction) {
        V value = map.get(key);
        if (Objects.nonNull(value)) {
            return value;
        }
        return map.computeIfAbsent(key, mappingFunction);
    }

    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }
}