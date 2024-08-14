package org.zero.common.core.util.java;

import java.util.Objects;

/**
 * @author Zero
 * @since 2016/1/14
 */
public class StringUtil {
    public static boolean isNull(String str) {
        return Objects.isNull(str);
    }

    public static boolean isNotNull(String str) {
        return !isNull(str);
    }

    public static boolean isEmpty(String str) {
        return isNull(str) || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(String str) {
        if (isNotEmpty(str)) {
            int length = str.length();
            for (int i = 0; i < length; i++) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String removePrefix(String str, String prefix) {
        return str.startsWith(prefix) ? str.substring(prefix.length()) : str;
    }

    public static String removeSuffix(String str, String suffix) {
        return str.endsWith(suffix) ? str.substring(0, str.length() - suffix.length()) : str;
    }

    public static String first2Lower(String str) {
        return prefix2Lower(str, 1);
    }

    public static String prefix2Lower(String str, int index) {
        return str.substring(0, index).toLowerCase() + str.substring(index);
    }

    public static String removePrefixAndFirst2Lower(String str, String prefix) {
        return first2Lower(removePrefix(str, prefix));
    }

    public static String removeSuffixAndFirst2Lower(String str, String suffix) {
        return first2Lower(removeSuffix(str, suffix));
    }

    private StringUtil() throws IllegalAccessException {
        throw new IllegalAccessException("Utility class");
    }
}
