package org.zero.common.core.util.java.util;

import org.zero.common.core.util.java.reflect.ClassUtil;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/10
 */
public class MapUtil {
    /**
     * The default initial capacity - MUST be a power of two.
     */
    public static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    public static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     */
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 默认容量
     */
    public static final int DEFAULT_CAPACITY = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR); // 12

    /**
     * The bin count threshold for using a tree rather than list for a
     * bin.  Bins are converted to trees when adding an element to a
     * bin with at least this many nodes. The value must be greater
     * than 2 and should be at least 8 to mesh with assumptions in
     * tree removal about conversion back to plain bins upon
     * shrinkage.
     */
    public static final int TREEIFY_THRESHOLD = 8;

    /**
     * The bin count threshold for untreeifying a (split) bin during a
     * resize operation. Should be less than TREEIFY_THRESHOLD, and at
     * most 6 to mesh with shrinkage detection under removal.
     */
    public static final int UNTREEIFY_THRESHOLD = 6;

    /**
     * The smallest table capacity for which bins may be treeified.
     * (Otherwise the table is resized if too many nodes in a bin.)
     * Should be at least 4 * TREEIFY_THRESHOLD to avoid conflicts
     * between resizing and treeification thresholds.
     */
    public static final int MIN_TREEIFY_CAPACITY = 64;

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return Objects.isNull(map) || map.isEmpty();
    }

    public static <K, V> boolean nonEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    public static <K, V> Map.Entry<K, V> entry(K key, V value, boolean canModifiable) {
        if (canModifiable) {
            return new AbstractMap.SimpleEntry<>(key, value);
        }
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return entry(key, value, true);
    }

    public static int calculateCapacity(int numMappings) {
        return (int) Math.ceil(numMappings / DEFAULT_LOAD_FACTOR);
    }

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

    public static <K, V> Optional<Map.Entry<K, V>> getFirstEntryOpt(Map<K, V> map) {
        if (isEmpty(map)){
            return Optional.empty();
        }
        return map.entrySet().stream().findFirst();
    }

    public static byte getByteValue(Map<?, ?> map, Object key) {
        return getValueOrDefault(map, key, Byte.class, (byte) 0);
    }

    public static short getShortValue(Map<?, ?> map, Object key) {
        return getValueOrDefault(map, key, Short.class, (short) 0);
    }

    public static int getIntValue(Map<?, ?> map, Object key) {
        return getValueOrDefault(map, key, Integer.class, 0);
    }

    public static long getLongValue(Map<?, ?> map, Object key) {
        return getValueOrDefault(map, key, Long.class, 0L);
    }

    public static float getFloatValue(Map<?, ?> map, Object key) {
        return getValueOrDefault(map, key, Float.class, 0.0F);
    }

    public static double getDoubleValue(Map<?, ?> map, Object key) {
        return getValueOrDefault(map, key, Double.class, 0.0D);
    }

    public static char getCharValue(Map<?, ?> map, Object key) {
        return getValueOrDefault(map, key, Character.class, '\u0000');
    }

    public static boolean getBoolValue(Map<?, ?> map, Object key) {
        return getValueOrDefault(map, key, Boolean.class, false);
    }

    public static <V> V getValueOrDefault(Map<?, ?> map, Object key, Type type, V defaultValue) {
        return MapUtil.<V>getValueOpt(map, key, type).orElse(defaultValue);
    }

    public static <V> Optional<V> getValueOpt(Map<?, ?> map, Object key, Type type) {
        return Optional.ofNullable(key)
                .map(map::get)
                .map(v -> ClassUtil.cast(v, type));
    }

    public static <K, V> Map<K, V> of(boolean isLinked, K key, V value) {
        return of(isLinked, Collections.singletonList(key), Collections.singletonList(value));
    }

    public static <K, V> Map<K, V> of(K key, V value) {
        return of(false, key, value);
    }

    public static <K, V> Map<K, V> of(boolean isLinked, K key1, V value1, K key2, V value2) {
        return of(isLinked, Arrays.asList(key1, key2), Arrays.asList(value1, value2));
    }

    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2) {
        return of(false, key1, value1, key2, value2);
    }

    public static <K, V> Map<K, V> of(boolean isLinked, K key1, V value1, K key2, V value2, K key3, V value3) {
        return of(isLinked, Arrays.asList(key1, key2, key3), Arrays.asList(value1, value2, value3));
    }

    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3) {
        return of(false, key1, value1, key2, value2, key3, value3);
    }

    public static <K, V> Map<K, V> of(boolean isLinked, K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
        return of(isLinked, Arrays.asList(key1, key2, key3, key4), Arrays.asList(value1, value2, value3, value4));
    }

    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
        return of(false, key1, value1, key2, value2, key3, value3, key4, value4);
    }

    public static <K, V> Map<K, V> of(boolean isLinked, K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5) {
        return of(isLinked, Arrays.asList(key1, key2, key3, key4, key5), Arrays.asList(value1, value2, value3, value4, value5));
    }

    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5) {
        return of(false, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5);
    }

    public static <K, V> Map<K, V> of(boolean isLinked, K[] keys, V[] values) {
        return of(isLinked, Arrays.asList(keys), Arrays.asList(values));
    }

    public static <K, V> Map<K, V> of(K[] keys, V[] values) {
        return of(false, keys, values);
    }

    public static <K, V> Map<K, V> of(boolean isLinked, Collection<K> keys, Collection<V> values) {
        int min = Math.min(keys.size(), values.size());
        Map<K, V> map = create(isLinked, min);
        Iterator<K> keyIterator = keys.iterator();
        Iterator<V> valueIterator = values.iterator();
        while (keyIterator.hasNext() && valueIterator.hasNext()) {
            map.put(keyIterator.next(), valueIterator.next());
        }
        return map;
    }

    public static <K, V> Map<K, V> of(Collection<K> keys, Collection<V> values) {
        return of(false, keys, values);
    }

    public static <K, V> Map<K, V> of(boolean isLinked, Collection<Object> keyValues) {
        return of(isLinked, keyValues.toArray());
    }

    public static <K, V> Map<K, V> of(Collection<Object> keyValues) {
        return of(false, keyValues);
    }

    public static <K, V> Map<K, V> of(boolean isLinked, Object... keyValues) {
        int length = keyValues.length;
        if ((length & 1) != 0) {
            throw new IllegalArgumentException("length is odd");
        }
        int size = length >> 1;
        Map<K, V> map = create(isLinked, size);
        for (int i = 0; i < length; i += 2) {
            @SuppressWarnings("unchecked")
            K key = (K) keyValues[i];
            @SuppressWarnings("unchecked")
            V value = (V) keyValues[i + 1];
            map.put(key, value);
        }
        return map;
    }

    public static <K, V> Map<K, V> of(Object... keyValues) {
        return of(false, keyValues);
    }

    public static <K, V> Map<K, V> create(boolean isLinked) {
        return create(isLinked, DEFAULT_CAPACITY);
    }

    public static <K, V> Map<K, V> create(boolean isLinked, int size) {
        int capacity = calculateCapacity(size);
        if (isLinked) {
            return new LinkedHashMap<>(capacity);
        } else {
            return new HashMap<>(capacity);
        }
    }

    protected MapUtil() {
        throw new UnsupportedOperationException();
    }
}