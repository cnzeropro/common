package org.zero.common.core.util.java.lang;

import java.nio.ByteBuffer;
import java.util.Arrays;

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
     * 合并两个 byte 数组（方式1）
     * <p>
     * 此处只做代码留存
     */
    protected static byte[] merge1(byte[] array1, byte[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        byte[] result = new byte[length1 + length2];
        System.arraycopy(array1, 0, result, 0, length1);
        System.arraycopy(array2, 0, result, length1, length2);
        return result;
    }

    /**
     * 合并两个 byte 数组（方式2）
     * <p>
     * 此处只做代码留存
     */
    protected static byte[] merge2(byte[] array1, byte[] array2) {
        ByteBuffer buffer = ByteBuffer.allocate(array1.length + array2.length);
        buffer.put(array1);
        buffer.put(array2);
        return buffer.array();
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

    private ArrayUtil() {
        throw new IllegalStateException("No instance");
    }
}
