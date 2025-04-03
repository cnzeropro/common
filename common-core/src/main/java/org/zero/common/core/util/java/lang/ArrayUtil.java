package org.zero.common.core.util.java.lang;

import org.zero.common.core.util.java.reflect.ClassUtil;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/11
 */
public class ArrayUtil {
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
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

    public static boolean isArray(Object source) {
        return ObjectUtil.nonNull(source) && ClassUtil.isArrayClass(source.getClass());
    }

    /**
     * 将指定对象转换为对象数组
     *
     * @param source 源对象
     * @return 数组
     */
    public static Object[] toArray(Object source) {
        if (Objects.isNull(source)) {
            return new Object[0];
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

    protected ArrayUtil() {
        throw new UnsupportedOperationException();
    }
}
