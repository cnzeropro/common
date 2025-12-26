package org.zero.common.core.util.java.lang;

import org.zero.common.data.constant.StringPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Zero
 * @since 2016/1/14
 */
public class StringUtil extends CharSequenceUtil implements StringPool {
	public static boolean equals(String str1, String str2, boolean ignoreCase) {
		if (isNull(str1)) {
			return isNull(str2);
		}
		if (isNull(str2)) {
			return false;
		}
		if (ignoreCase) {
			return str1.equalsIgnoreCase(str2);
		}
		return str1.contentEquals(str2);
	}

	public static boolean subEquals(String str1, int from, String str2, boolean ignoreCase) {
		if (isNull(str1) || isNull(str2)) {
			return false;
		}
		return subEquals(str1, from, str2, 0, str2.length(), ignoreCase);
	}

	public static boolean subEquals(String str1, int from1, String str2, int from2, int length, boolean ignoreCase) {
		if (isNull(str1) || isNull(str2)) {
			return false;
		}
		return str1.regionMatches(ignoreCase, from1, str2, from2, length);
	}

	public static int indexOf(String text, String search, boolean ignoreCase) {
		return indexOf(text, search, 0, ignoreCase);
	}

	public static int indexOf(String text, String search, int from, boolean ignoreCase) {
		return indexOf(text, search, from, INDEX_NOT_FOUND, ignoreCase);
	}

	public static int indexOf(String text, String search, int start, int end, boolean ignoreCase) {
		if (isNull(text) || isNull(search)) {
			return INDEX_NOT_FOUND;
		}
		if (isEmpty(text) || isEmpty(search)) {
			if (equals(text, search, ignoreCase)) {
				return 0;
			}
			return INDEX_NOT_FOUND;
		}
		int textLength = text.length();
		if (start < 0) {
			start = 0;
		}
		int limit;
		if (end < 0) {
			limit = textLength;
		} else {
			limit = Math.min(end, textLength);
		}
		limit = limit - search.length() + 1;
		for (int i = start; i < limit; i++) {
			if (subEquals(text, i, search, ignoreCase)) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	public static boolean startWith(String string, String prefix, boolean ignoreCase) {
		if (Objects.equals(string, prefix)) {
			return true;
		}
		if (Objects.isNull(string) || Objects.isNull(prefix)) {
			return false;
		}
		return string.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
	}

	public static boolean endWith(String string, String suffix, boolean ignoreCase) {
		if (Objects.equals(string, suffix)) {
			return true;
		}
		if (Objects.isNull(string) || Objects.isNull(suffix)) {
			return false;
		}
		return string.regionMatches(ignoreCase, string.length() - suffix.length(), suffix, 0, suffix.length());
	}

	public static String sub(String string, int fromIndexInclude) {
		return sub(string, fromIndexInclude, string.length());
	}

	public static String sub(String string, int fromIndexInclude, int toIndexExclude) {
		if (isEmpty(string)) {
			return string;
		}
		return CharSequenceUtil.sub(string, fromIndexInclude, toIndexExclude).toString();
	}

	public static String trimStart(String string) {
		return trim(string, TrimMode.START);
	}

	public static String trimStart(String string, Predicate<Character> predicate) {
		return trim(string, TrimMode.START, predicate);
	}

	public static String trimEnd(String string) {
		return trim(string, TrimMode.END);
	}

	public static String trimEnd(String string, Predicate<Character> predicate) {
		return trim(string, TrimMode.END, predicate);
	}

	public static String trim(String string) {
		return trim(string, TrimMode.ALL);
	}

	public static String trim(String string, TrimMode mode) {
		return trim(string, mode, Character::isWhitespace);
	}

	public static String trim(String string, TrimMode mode, Predicate<Character> predicate) {
		if (isNull(string)) {
			return null;
		}
		CharSequence charSequence = CharSequenceUtil.trim(string, mode, predicate);
		return toString(charSequence);
	}

	public static String removePrefix(String string, String prefix, boolean ignoreCase) {
		if (isEmpty(string) || isEmpty(prefix)) {
			return string;
		}
		if (startWith(string, prefix, ignoreCase)) {
			return sub(string, prefix.length(), string.length());
		}
		return string;
	}

	public static String removeSuffix(String string, String suffix, boolean ignoreCase) {
		if (isEmpty(string) || isEmpty(suffix)) {
			return string;
		}
		if (endWith(string, suffix, ignoreCase)) {
			return sub(string, 0, string.length() - suffix.length());
		}
		return string;
	}

	public static String firstToLower(String string) {
		return prefixToLower(string, 1);
	}

	public static String prefixToLower(String string, int index) {
		if (isBlank(string) || string.length() > index) {
			return string;
		}
		return string.substring(0, index).toLowerCase() + string.substring(index);
	}

	public static String removePrefixAndFirstToLower(String string, String prefix, boolean ignoreCase) {
		return firstToLower(removePrefix(string, prefix, ignoreCase));
	}

	public static String removeSuffixAndFirstToLower(String string, String suffix, boolean ignoreCase) {
		return firstToLower(removeSuffix(string, suffix, ignoreCase));
	}

	public static boolean contains(String string, String searchString, boolean ignoreCase) {
		if (isNull(string) || isNull(searchString)) {
			return false;
		}
		return indexOf(string, searchString, ignoreCase) > INDEX_NOT_FOUND;
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

	public static Collection<String> split(String string, String delimiter) {
		return split(string, delimiter, false);
	}

	public static Collection<String> split(String string, String delimiter, boolean isTrim) {
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
				result.add(isTrim ? charStr.trim() : charStr);
			}
		} else {
			int delimiterLength = delimiter.length();
			int fromIndex = 0;
			int findIndex;
			while ((findIndex = string.indexOf(delimiter, fromIndex)) != -1) {
				String subStr = string.substring(fromIndex, findIndex);
				result.add(isTrim ? subStr.trim() : subStr);
				fromIndex = findIndex + delimiterLength;
			}
			if (!string.isEmpty() && fromIndex <= string.length()) {
				String subStr = string.substring(fromIndex);
				result.add(isTrim ? subStr.trim() : subStr);
			}
		}
		return result;
	}

	public static String toString(Object obj) {
		return toString(obj, null);
	}

	public static String toString(Object obj, String defaultValue) {
		return Objects.isNull(obj) ? defaultValue : obj.toString();
	}

	protected StringUtil() {
		throw new UnsupportedOperationException();
	}
}
