package org.zero.common.core.util.java.lang;

import org.zero.common.core.extension.java.util.function.ToBoolFunction;
import org.zero.common.core.extension.java.util.function.ToByteFunction;
import org.zero.common.core.util.java.reflect.ClassUtil;
import org.zero.common.core.util.java.util.CollectionUtil;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/11
 */
public class ArrayUtil {
    /**
     * 数组中元素未找到的下标，值为 -1
     */
    public static final int INDEX_NOT_FOUND = -1;
    /**
     * Java 虚拟机最大数组长度
     */
    public static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 2;
    /**
     * The maximum size of array to allocate (unless necessary).
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    public static final int SAFE_MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    /**
     * 空数组
     */
    public static final Object[] EMPTY = {};

    public static boolean isArray(Object source) {
        return ObjectUtil.nonNull(source) && ClassUtil.isArray(source.getClass());
    }

    public static int length(Object array) {
        if (Objects.isNull(array)) {
            return 0;
        }
        return Array.getLength(array);
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean nonEmpty(Object[] array) {
        return !isEmpty(array);
    }

    /**
     * 将指定对象转换为对象数组
     *
     * @param source 源对象
     * @return 数组
     */
    public static Object[] toArray(Object source) {
        if (Objects.isNull(source)) {
            return EMPTY;
        }
        Class<?> clazz = source.getClass();
        if (clazz.isArray()) {
            Class<?> componentType = clazz.getComponentType();
            // 处理原始类型数组（如int[]）
            // 因为原始类型数组继承自 Object，因此无法使用 (Object[]) source 强转
            // 使用 source instanceof int[]、 source instanceof double[] 等等一个一个判断又过于麻烦，因此原始类型数组统一处理
            if (componentType.isPrimitive()) {
                int length = Array.getLength(source);
                // 获取原始类型的包装类型
                Class<?> wrappedType = PrimitiveType.wrap(componentType);
                // 创建包装类型数组（如果不使用包装类创建数组，此处无法使用 Object[] 强转）
                Object[] array = (Object[]) Array.newInstance(wrappedType, length);
                for (int i = 0; i < length; i++) {
                    array[i] = Array.get(source, i);
                }
                return array;
            }
            // 处理对象数组（如String[]）
            return (Object[]) source;
        }
        return new Object[]{source};
    }

    public static <T> T[] toArray(Collection<T> collection, Class<T> componentType) {
        T[] array = create(componentType, 0);
        if (CollectionUtil.isEmpty(collection)) {
            return array;
        }
        return collection.toArray(array);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] create(Class<?> componentType, int length) {
        return (T[]) Array.newInstance(componentType, length);
    }

    public static <T> T[] of(Class<T> componentType, T t) {
        T[] array = create(componentType, 1);
        Array.set(array, 0, t);
        return array;
    }

    public static <T> T[] of(Class<T> componentType, T t1, T t2) {
        T[] array = create(componentType, 2);
        Array.set(array, 0, t1);
        Array.set(array, 1, t2);
        return array;
    }

    public static <T> T[] of(Class<T> componentType, T t1, T t2, T t3) {
        T[] array = create(componentType, 3);
        Array.set(array, 0, t1);
        Array.set(array, 1, t2);
        Array.set(array, 2, t3);
        return array;
    }

    @SafeVarargs
    public static <T> T[] of(Class<T> componentType, T... ts) {
        T[] array = create(componentType, ts.length);
        System.arraycopy(ts, 0, array, 0, ts.length);
        return array;
    }

    /**
     * 追加 byte 数组内容
     */
    public static byte[] append(byte[] array, byte... bytes) {
        return merge(array, bytes);
    }

    /**
     * 追加 short 数组内容
     */
    public static short[] append(short[] array, short... shorts) {
        return merge(array, shorts);
    }

    /**
     * 追加 int 数组内容
     */
    public static int[] append(int[] array, int... ints) {
        return merge(array, ints);
    }

    /**
     * 追加 long 数组内容
     */
    public static long[] append(long[] array, long... longs) {
        return merge(array, longs);
    }

    /**
     * 追加 float 数组内容
     */
    public static float[] append(float[] array, float... floats) {
        return merge(array, floats);
    }

    /**
     * 追加 double 数组内容
     */
    public static double[] append(double[] array, double... doubles) {
        return merge(array, doubles);
    }

    /**
     * 追加 char 数组内容
     */
    public static char[] append(char[] array, char... chars) {
        return merge(array, chars);
    }

    /**
     * 追加 boolean 数组内容
     */
    public static boolean[] append(boolean[] array, boolean... booleans) {
        return merge(array, booleans);
    }

    /**
     * 追加泛型数组内容
     * <p>
     * 切勿乱用，造成堆污染
     */
    @SafeVarargs
    public static <T> T[] append(T[] array, T... objects) {
        return merge(array, objects);
    }

    /**
     * 合并多个 byte 数组
     */
    public static byte[] merge(byte[] array, byte[]... arrays) {
        int length = array.length;
        for (byte[] a : arrays) {
            length += a.length;
        }
        byte[] result = Arrays.copyOf(array, length);
        int pos = array.length;
        for (byte[] a : arrays) {
            System.arraycopy(a, 0, result, pos, a.length);
            pos += a.length;
        }
        return result;
    }

    /**
     * 合并多个 short 数组
     */
    public static short[] merge(short[] array, short[]... arrays) {
        int length = array.length;
        for (short[] a : arrays) {
            length += a.length;
        }
        short[] result = Arrays.copyOf(array, length);
        int pos = array.length;
        for (short[] a : arrays) {
            System.arraycopy(a, 0, result, pos, a.length);
            pos += a.length;
        }
        return result;
    }

    /**
     * 合并多个 int 数组
     */
    public static int[] merge(int[] array, int[]... arrays) {
        int length = array.length;
        for (int[] a : arrays) {
            length += a.length;
        }
        int[] result = Arrays.copyOf(array, length);
        int pos = array.length;
        for (int[] a : arrays) {
            System.arraycopy(a, 0, result, pos, a.length);
            pos += a.length;
        }
        return result;
    }

    /**
     * 合并多个 long 数组
     */
    public static long[] merge(long[] array, long[]... arrays) {
        int length = array.length;
        for (long[] a : arrays) {
            length += a.length;
        }
        long[] result = Arrays.copyOf(array, length);
        int pos = array.length;
        for (long[] a : arrays) {
            System.arraycopy(a, 0, result, pos, a.length);
            pos += a.length;
        }
        return result;
    }

    /**
     * 合并多个 float 数组
     */
    public static float[] merge(float[] array, float[]... arrays) {
        int length = array.length;
        for (float[] a : arrays) {
            length += a.length;
        }
        float[] result = Arrays.copyOf(array, length);
        int pos = array.length;
        for (float[] a : arrays) {
            System.arraycopy(a, 0, result, pos, a.length);
            pos += a.length;
        }
        return result;
    }

    /**
     * 合并多个 double 数组
     */
    public static double[] merge(double[] array, double[]... arrays) {
        int length = array.length;
        for (double[] a : arrays) {
            length += a.length;
        }
        double[] result = Arrays.copyOf(array, length);
        int pos = array.length;
        for (double[] a : arrays) {
            System.arraycopy(a, 0, result, pos, a.length);
            pos += a.length;
        }
        return result;
    }

    /**
     * 合并多个 char 数组
     */
    public static char[] merge(char[] array, char[]... arrays) {
        int length = array.length;
        for (char[] a : arrays) {
            length += a.length;
        }
        char[] result = Arrays.copyOf(array, length);
        int pos = array.length;
        for (char[] a : arrays) {
            System.arraycopy(a, 0, result, pos, a.length);
            pos += a.length;
        }
        return result;
    }

    /**
     * 合并多个 boolean 数组
     */
    public static boolean[] merge(boolean[] array, boolean[]... arrays) {
        int length = array.length;
        for (boolean[] a : arrays) {
            length += a.length;
        }
        boolean[] result = Arrays.copyOf(array, length);
        int pos = array.length;
        for (boolean[] a : arrays) {
            System.arraycopy(a, 0, result, pos, a.length);
            pos += a.length;
        }
        return result;
    }

    /**
     * 合并多个泛型数组
     * <p>
     * 切勿乱用，造成堆污染
     */
    @SafeVarargs
    public static <T> T[] merge(T[] array, T[]... arrays) {
        int length = array.length;
        for (T[] a : arrays) {
            length += a.length;
        }
        T[] result = Arrays.copyOf(array, length);
        int pos = array.length;
        for (T[] a : arrays) {
            System.arraycopy(a, 0, result, pos, a.length);
            pos += a.length;
        }
        return result;
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @param <T>   数组元素类型
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static <T> T[] sub(T[] array, int start, int end) {
        if (isEmpty(array)) {
            return array;
        }
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return create(array.getClass().getComponentType(), 0);
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return create(array.getClass().getComponentType(), 0);
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    public static <T> byte[] map(T[] array, ToByteFunction<T> function) {
        if (isEmpty(array)) {
            return new byte[0];
        }
        byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = function.applyAsByte(array[i]);
        }
        return result;
    }

    public static <T> boolean[] map(T[] array, ToBoolFunction<T> function) {
        if (isEmpty(array)) {
            return new boolean[0];
        }
        boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = function.applyAsBool(array[i]);
        }
        return result;
    }

    /**
     * 数组映射
     * <p>
     * <b>注意：映射函数必须要处理原始元素为 null 也返回有值（不为 null）的情况</b>
     *
     * @param array    原始数组
     * @param function 映射函数
     * @param <T>      原始数组元素类型
     * @param <R>      映射后的数组元素类型
     * @return 映射后的数组
     */
    public static <T, R> R[] map(T[] array, Function<T, R> function) {
        @SuppressWarnings("unchecked")
        Class<R> clazz = (Class<R>) function.apply(null).getClass();
        return map(array, function, clazz);
    }

    /**
     * 数组映射
     *
     * @param array    原始数组
     * @param function 映射函数
     * @param <T>      原始数组元素类型
     * @param <R>      映射后的数组元素类型
     * @return 映射后的数组
     */
    @SuppressWarnings("unchecked")
    public static <T, R> R[] map(T[] array, Function<T, R> function, Class<R> clazz) {
        if (isEmpty(array)) {
            return create(clazz, 0);
        }
        return Arrays.stream(array)
                .map(function)
                .toArray(size -> (R[]) Array.newInstance(clazz, size));
    }

    public static <T> String join(T[] array, CharSequence delimiter) {
        return Arrays.stream(array)
                .filter(Objects::nonNull)
                .map(Objects::toString)
                .collect(Collectors.joining(delimiter));
    }

    public static <T> T get(T[] array, int index) {
        return getOpt(array, index).orElse(null);
    }

    public static <T> Optional<T> getOpt(T[] array, int index) {
        if (isEmpty(array)) {
            return Optional.empty();
        }
        return index > INDEX_NOT_FOUND && index < array.length ? Optional.ofNullable(array[index]) : Optional.empty();
    }

    protected ArrayUtil() {
        throw new UnsupportedOperationException();
    }
}
