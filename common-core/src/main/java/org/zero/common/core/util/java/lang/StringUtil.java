package org.zero.common.core.util.java.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Zero
 * @since 2016/1/14
 */
public class StringUtil extends CharSequenceUtil {

    public static String removePrefix(String string, String prefix) {
        return string.startsWith(prefix) ? string.substring(prefix.length()) : string;
    }

    public static String removeSuffix(String string, String suffix) {
        return string.endsWith(suffix) ? string.substring(0, string.length() - suffix.length()) : string;
    }

    public static String firstToLower(String string) {
        return prefixToLower(string, 1);
    }

    public static String prefixToLower(String string, int index) {
        return string.substring(0, index).toLowerCase() + string.substring(index);
    }

    public static String removePrefixAndFirstToLower(String string, String prefix) {
        return firstToLower(removePrefix(string, prefix));
    }

    public static String removeSuffixAndFirstToLower(String string, String suffix) {
        return firstToLower(removeSuffix(string, suffix));
    }

    public static boolean containsAny(String string, String... searchStrings) {
        if (isNull(string) || ArrayUtil.isEmpty(searchStrings)) {
            return false;
        }

        for (String searchString : searchStrings) {
            if (string.contains(searchString)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsAll(String string, String... searchStrings) {
        if (isNull(string) || ArrayUtil.isEmpty(searchStrings)) {
            return false;
        }

        for (String searchString : searchStrings) {
            if (!string.contains(searchString)) {
                return false;
            }
        }
        return true;
    }

    public static List<String> split(String string, String delimiter) {
        if (Objects.isNull(string)) {
            return Collections.emptyList();
        }
        if (Objects.isNull(delimiter)) {
            return Collections.singletonList(string);
        }

        List<String> result = new ArrayList<>();
        if (delimiter.isEmpty()) {
            for (int i = 0; i < string.length(); i++) {
                String charStr = string.substring(i, i + 1);
                result.add(charStr);
            }
        } else {
            int delimiterLength = delimiter.length();
            int fromIndex = 0;
            int findIndex;
            while ((findIndex = string.indexOf(delimiter, fromIndex)) != -1) {
                String subStr = string.substring(fromIndex, findIndex);
                result.add(subStr);
                fromIndex = findIndex + delimiterLength;
            }
            if (!string.isEmpty() && fromIndex <= string.length()) {
                String subStr = string.substring(fromIndex);
                result.add(subStr);
            }
        }
        return result;
    }

    protected StringUtil() {
        throw new UnsupportedOperationException();
    }
}
