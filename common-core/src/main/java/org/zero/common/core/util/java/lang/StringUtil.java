package org.zero.common.core.util.java.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public static boolean containsAny(String str, String... testStrs) {
        return isNotNull(getContainsStr(str, testStrs));
    }

    public static String getContainsStr(String str, String... testStrs) {
        if (isEmpty(str) || ArrayUtil.isEmpty(testStrs)) {
            return null;
        }
        for (String checkStr : testStrs) {
            if (isNotNull(str) && str.contains(checkStr)) {
                return checkStr;
            }
        }
        return null;
    }

    public static List<String> split(String str, String delimiter) {
        if (Objects.isNull(str)) {
            return Collections.emptyList();
        }
        if (Objects.isNull(delimiter)) {
            return Collections.singletonList(str);
        }

        List<String> result = new ArrayList<>();
        if (delimiter.isEmpty()) {
            for (int i = 0; i < str.length(); i++) {
                String charStr = str.substring(i, i + 1);
                result.add(charStr);
            }
        } else {
            int delimiterLength = delimiter.length();
            int fromIndex = 0;
            int findIndex;
            while ((findIndex = str.indexOf(delimiter, fromIndex)) != -1) {
                String subStr = str.substring(fromIndex, findIndex);
                result.add(subStr);
                fromIndex = findIndex + delimiterLength;
            }
            if (!str.isEmpty() && fromIndex <= str.length()) {
                String subStr = str.substring(fromIndex);
                result.add(subStr);
            }
        }
        return result;
    }

    private StringUtil() throws IllegalAccessException {
        throw new IllegalAccessException("Utility class");
    }
}
