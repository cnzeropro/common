package org.zero.common.core.util.java.math;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 组合计算工具类
 *
 * @author Zero
 */
public class CombinatoricsUtil {
    private CombinatoricsUtil() {
    }

    private static final String SEPARATOR = "\0";

    /**
     * 根据字符串计算出全部组合
     *
     * @param sourceStr 初始化字符串源信息
     *                  例如：“1,2,3,4,5,6,7”,"a|b|c|d","pp-7-fcw-y?s-9o"等
     * @param regex     分割字串
     *                  如上述例子中：","，"|"，"-"等
     * @param len       每种组合的长度
     *                  如上述第三例："ppy?c","fcw7"算长度2
     * @return 组合
     */
    public static String[] getCombinatoricsArray(String sourceStr, String regex, int len) {
        return getCombinatoricsSet(sourceStr, regex, len).toArray(new String[0]);
    }

    /**
     * 根据字符串计算出全部组合
     *
     * @param sourceStr 初始化字符串源信息
     *                  例如：“1,2,3,4,5,6,7”,"a|b|c|d","pp-7-fcw-y?s-9o"等
     * @param regex     分割字串
     *                  如上述例子中：","，"|"，"-"等
     * @param len       每种组合的长度
     *                  如上述第三例："ppy?c","fcw7"算长度2
     * @return 组合
     */
    public static Set<String> getCombinatoricsSet(String sourceStr, String regex, int len) {
        String[] sourceArray = sourceStr.split(regex);
        return getCombinatoricsSet(sourceArray, len);
    }

    /**
     * 根据字符串数组计算出全部组合
     *
     * @param sourceArray 初始化字符串数组源信息
     * @param len         每种组合的长度
     * @return 组合
     */
    public static String[] getCombinatoricsArray(String[] sourceArray, int len) {
        return getCombinatoricsSet(sourceArray, len).toArray(new String[0]);
    }

    /**
     * 根据字符串数组计算出全部组合
     *
     * @param sourceArray 初始化字符串数组源信息
     * @param len         每种组合的长度
     * @return 组合
     */
    public static Set<String> getCombinatoricsSet(String[] sourceArray, int len) {
        Set<String> result = new LinkedHashSet<>();
        for (String s : sourceArray) {
            Set<String> strings = doCalculate(s, sourceArray, len);
            result.addAll(strings);
        }
        return result;
    }

    /**
     * 计算组合
     *
     * @param start       组合开始字串
     * @param sourceArray 字符串数组源信息
     * @param len         组合长度
     * @return 组合
     */
    private static Set<String> doCalculate(String start, String[] sourceArray, int len) {
        Set<String> result = new LinkedHashSet<>();
        String[] olds = start.split(SEPARATOR);
        if (olds.length != len) {
            for (String value : sourceArray) {
                if (Arrays.binarySearch(olds, value) < 0) {
                    Set<String> strings = doCalculate(start + SEPARATOR + value, sourceArray, len);
                    result.addAll(strings);
                }
            }
        } else {
            String replaced = start.replace(SEPARATOR, "");
            result.add(replaced);
        }
        return result;
    }
}
