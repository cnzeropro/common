package org.zero.common.core.util.java.lang;

import org.zero.common.core.extension.java.util.function.ToBoolFunction;
import org.zero.common.core.extension.java.util.function.ToByteFunction;
import org.zero.common.core.extension.java.util.function.ToCharFunction;
import org.zero.common.core.extension.java.util.function.ToFloatFunction;
import org.zero.common.core.extension.java.util.function.ToShortFunction;
import org.zero.common.data.constant.ConstantPool;
import org.zero.common.data.constant.StringPool;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/1
 */
public class CharSequenceUtil {
	public static final int INDEX_NOT_FOUND = ArrayUtil.INDEX_NOT_FOUND;

	public static boolean isNull(CharSequence charSequence) {
		return ObjectUtil.isNull(charSequence);
	}

	public static boolean nonNull(CharSequence charSequence) {
		return ObjectUtil.nonNull(charSequence);
	}

	public static boolean isEmpty(CharSequence charSequence) {
		return isNull(charSequence) || charSequence.length() == 0;
	}

	public static boolean nonEmpty(CharSequence charSequence) {
		return !isEmpty(charSequence);
	}

	public static boolean isBlank(CharSequence charSequence) {
		if (nonEmpty(charSequence)) {
			int length = charSequence.length();
			for (int i = 0; i < length; i++) {
				if (!Character.isWhitespace(charSequence.charAt(i))) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean nonBlank(CharSequence charSequence) {
		return !isBlank(charSequence);
	}

	public static int indexOf(CharSequence charSequence, CharSequence searchCharSequence, boolean ignoreCase) {
		return indexOf(charSequence, searchCharSequence, 0, ignoreCase);
	}

	public static int indexOf(CharSequence charSequence, CharSequence searchCharSequence, int from, boolean ignoreCase) {
		if (isEmpty(charSequence) || isEmpty(searchCharSequence)) {
			return contentEquals(charSequence, searchCharSequence, ignoreCase) ? 0 : INDEX_NOT_FOUND;
		}
		int length = charSequence.length();
		int searchLength = searchCharSequence.length();
		if (from < 0) {
			from = 0;
		}
		if (from > length || searchLength > length) {
			return INDEX_NOT_FOUND;
		}
		if (searchLength == 0) {
			return from;
		}
		int end = length - searchLength;
		if (from > end) {
			return INDEX_NOT_FOUND;
		}

		for (int i = from; i <= end; i++) {
			boolean found = true;
			// 检查当前位置开始的子序列是否匹配
			for (int j = 0; j < searchLength; j++) {
				char sourceChar = charSequence.charAt(i + j);
				char targetChar = searchCharSequence.charAt(j);

				if (ignoreCase) {
					if (Character.toLowerCase(sourceChar) != Character.toLowerCase(targetChar) &&
						Character.toUpperCase(sourceChar) != Character.toUpperCase(targetChar)) {
						found = false;
						break;
					}
				} else {
					if (sourceChar != targetChar) {
						found = false;
						break;
					}
				}
			}
			if (found) {
				return i;
			}
		}

		return INDEX_NOT_FOUND;
	}

	public static int lastIndexOf(CharSequence charSequence, CharSequence searchCharSequence, boolean ignoreCase) {
		return lastIndexOf(charSequence, searchCharSequence, charSequence.length(), ignoreCase);
	}

	public static int lastIndexOf(CharSequence charSequence, CharSequence searchCharSequence, int from, boolean ignoreCase) {
		if (isEmpty(charSequence) || isEmpty(searchCharSequence)) {
			return contentEquals(charSequence, searchCharSequence, ignoreCase) ? 0 : INDEX_NOT_FOUND;
		}
		int length = charSequence.length();
		int searchLength = searchCharSequence.length();
		if (from < 0 || searchLength > length) {
			return INDEX_NOT_FOUND;
		}
		// 计算实际开始搜索的位置
		int start = Math.min(from, length - searchLength);
		for (int i = start; i >= 0; i--) {
			boolean found = true;
			// 检查当前位置开始的子序列是否匹配
			for (int j = 0; j < searchLength; j++) {
				char sourceChar = charSequence.charAt(i + j);
				char targetChar = searchCharSequence.charAt(j);
				if (ignoreCase) {
					if (Character.toLowerCase(sourceChar) != Character.toLowerCase(targetChar) &&
						Character.toUpperCase(sourceChar) != Character.toUpperCase(targetChar)) {
						found = false;
						break;
					}
				} else {
					if (sourceChar != targetChar) {
						found = false;
						break;
					}
				}
			}

			if (found) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	public static CharSequence sub(CharSequence charSequence, int fromIndexInclude) {
		return sub(charSequence, fromIndexInclude, charSequence.length());
	}

	public static CharSequence sub(CharSequence charSequence, int fromIndexInclude, int toIndexExclude) {
		if (isEmpty(charSequence)) {
			return charSequence;
		}
		int len = charSequence.length();

		if (fromIndexInclude < 0) {
			fromIndexInclude = len + fromIndexInclude;
			if (fromIndexInclude < 0) {
				fromIndexInclude = 0;
			}
		} else if (fromIndexInclude > len) {
			fromIndexInclude = len;
		}

		if (toIndexExclude < 0) {
			toIndexExclude = len + toIndexExclude;
			if (toIndexExclude < 0) {
				toIndexExclude = len;
			}
		} else if (toIndexExclude > len) {
			toIndexExclude = len;
		}

		if (toIndexExclude < fromIndexInclude) {
			int tmp = fromIndexInclude;
			fromIndexInclude = toIndexExclude;
			toIndexExclude = tmp;
		}

		if (fromIndexInclude == toIndexExclude) {
			return StringPool.EMPTY;
		}
		return charSequence.subSequence(fromIndexInclude, toIndexExclude);
	}

	public static boolean contentEquals(CharSequence charSequence, CharSequence otherCharSequence) {
		return contentEquals(charSequence, otherCharSequence, false);
	}

	public static boolean contentEquals(CharSequence charSequence, CharSequence otherCharSequence, boolean ignoreCase) {
		if (nonNull(charSequence) && nonNull(otherCharSequence)) {
			if (ignoreCase) {
				return charSequence.toString().equalsIgnoreCase(otherCharSequence.toString());
			}
			if (charSequence instanceof String) {
				return ((String) charSequence).contentEquals(otherCharSequence);
			}
			if (otherCharSequence instanceof String) {
				return ((String) otherCharSequence).contentEquals(charSequence);
			}
			return charSequence.toString().contentEquals(otherCharSequence);
		}
		return isNull(charSequence) && isNull(otherCharSequence);
	}

	public static <T extends CharSequence> T defaultIfNull(final T charSequence, final T defaultCharSequence) {
		return isNull(charSequence) ? defaultCharSequence : charSequence;
	}

	public static <T extends CharSequence> T defaultIfEmpty(final T charSequence, final T defaultCharSequence) {
		return isEmpty(charSequence) ? defaultCharSequence : charSequence;
	}

	public static <T extends CharSequence> T defaultIfBlank(final T charSequence, final T defaultCharSequence) {
		return isBlank(charSequence) ? defaultCharSequence : charSequence;
	}

	/* ************************************************ mapIfNonNull ************************************************ */
	public static <T extends CharSequence, R> R mapIfNonNull(final T charSequence, Function<T, R> mapper) {
		return nonNull(charSequence) ? mapper.apply(charSequence) : null;
	}

	public static <T extends CharSequence> byte mapIfNonNull(final T charSequence, ToByteFunction<T> mapper) {
		return nonNull(charSequence) ? mapper.applyAsByte(charSequence) : ConstantPool.BYTE_ZERO;
	}

	public static <T extends CharSequence> short mapIfNonNull(final T charSequence, ToShortFunction<T> mapper) {
		return nonNull(charSequence) ? mapper.applyAsShort(charSequence) : ConstantPool.SHORT_ZERO;
	}

	public static <T extends CharSequence> int mapIfNonNull(final T charSequence, ToIntFunction<T> mapper) {
		return nonNull(charSequence) ? mapper.applyAsInt(charSequence) : ConstantPool.INT_ZERO;
	}

	public static <T extends CharSequence> long mapIfNonNull(final T charSequence, ToLongFunction<T> mapper) {
		return nonNull(charSequence) ? mapper.applyAsLong(charSequence) : ConstantPool.LONG_ZERO;
	}

	public static <T extends CharSequence> float mapIfNonNull(final T charSequence, ToFloatFunction<T> mapper) {
		return nonNull(charSequence) ? mapper.applyAsFloat(charSequence) : ConstantPool.FLOAT_ZERO;
	}

	public static <T extends CharSequence> double mapIfNonNull(final T charSequence, ToDoubleFunction<T> mapper) {
		return nonNull(charSequence) ? mapper.applyAsDouble(charSequence) : ConstantPool.DOUBLE_ZERO;
	}

	public static <T extends CharSequence> char mapIfNonNull(final T charSequence, ToCharFunction<T> mapper) {
		return nonNull(charSequence) ? mapper.applyAsChar(charSequence) : ConstantPool.CHAR_ZERO;
	}

	public static <T extends CharSequence> boolean mapIfNonNull(final T charSequence, ToBoolFunction<T> mapper) {
		return nonNull(charSequence) ? mapper.applyAsBool(charSequence) : ConstantPool.BOOLEAN_FALSE;
	}

	/* ************************************************ mapIfNonEmpty ************************************************ */
	public static <T extends CharSequence, R> R mapIfNonEmpty(final T charSequence, Function<T, R> mapper) {
		return nonEmpty(charSequence) ? mapper.apply(charSequence) : null;
	}

	public static <T extends CharSequence> byte mapIfNonEmpty(final T charSequence, ToByteFunction<T> mapper) {
		return nonEmpty(charSequence) ? mapper.applyAsByte(charSequence) : ConstantPool.BYTE_ZERO;
	}

	public static <T extends CharSequence> short mapIfNonEmpty(final T charSequence, ToShortFunction<T> mapper) {
		return nonEmpty(charSequence) ? mapper.applyAsShort(charSequence) : ConstantPool.SHORT_ZERO;
	}

	public static <T extends CharSequence> int mapIfNonEmpty(final T charSequence, ToIntFunction<T> mapper) {
		return nonEmpty(charSequence) ? mapper.applyAsInt(charSequence) : ConstantPool.INT_ZERO;
	}

	public static <T extends CharSequence> long mapIfNonEmpty(final T charSequence, ToLongFunction<T> mapper) {
		return nonEmpty(charSequence) ? mapper.applyAsLong(charSequence) : ConstantPool.LONG_ZERO;
	}

	public static <T extends CharSequence> float mapIfNonEmpty(final T charSequence, ToFloatFunction<T> mapper) {
		return nonEmpty(charSequence) ? mapper.applyAsFloat(charSequence) : ConstantPool.FLOAT_ZERO;
	}

	public static <T extends CharSequence> double mapIfNonEmpty(final T charSequence, ToDoubleFunction<T> mapper) {
		return nonEmpty(charSequence) ? mapper.applyAsDouble(charSequence) : ConstantPool.DOUBLE_ZERO;
	}

	public static <T extends CharSequence> char mapIfNonEmpty(final T charSequence, ToCharFunction<T> mapper) {
		return nonEmpty(charSequence) ? mapper.applyAsChar(charSequence) : ConstantPool.CHAR_ZERO;
	}

	public static <T extends CharSequence> boolean mapIfNonEmpty(final T charSequence, ToBoolFunction<T> mapper) {
		return nonEmpty(charSequence) ? mapper.applyAsBool(charSequence) : ConstantPool.BOOLEAN_FALSE;
	}

	/* ************************************************ mapIfNonBlank ************************************************ */
	public static <T extends CharSequence, R> R mapIfNonBlank(final T charSequence, Function<T, R> mapper) {
		return nonBlank(charSequence) ? mapper.apply(charSequence) : null;
	}

	public static <T extends CharSequence> byte mapIfNonBlank(final T charSequence, ToByteFunction<T> mapper) {
		return nonBlank(charSequence) ? mapper.applyAsByte(charSequence) : ConstantPool.BYTE_ZERO;
	}

	public static <T extends CharSequence> short mapIfNonBlank(final T charSequence, ToShortFunction<T> mapper) {
		return nonBlank(charSequence) ? mapper.applyAsShort(charSequence) : ConstantPool.SHORT_ZERO;
	}

	public static <T extends CharSequence> int mapIfNonBlank(final T charSequence, ToIntFunction<T> mapper) {
		return nonBlank(charSequence) ? mapper.applyAsInt(charSequence) : ConstantPool.INT_ZERO;
	}

	public static <T extends CharSequence> long mapIfNonBlank(final T charSequence, ToLongFunction<T> mapper) {
		return nonBlank(charSequence) ? mapper.applyAsLong(charSequence) : ConstantPool.LONG_ZERO;
	}

	public static <T extends CharSequence> float mapIfNonBlank(final T charSequence, ToFloatFunction<T> mapper) {
		return nonBlank(charSequence) ? mapper.applyAsFloat(charSequence) : ConstantPool.FLOAT_ZERO;
	}

	public static <T extends CharSequence> double mapIfNonBlank(final T charSequence, ToDoubleFunction<T> mapper) {
		return nonBlank(charSequence) ? mapper.applyAsDouble(charSequence) : ConstantPool.DOUBLE_ZERO;
	}

	public static <T extends CharSequence> char mapIfNonBlank(final T charSequence, ToCharFunction<T> mapper) {
		return nonBlank(charSequence) ? mapper.applyAsChar(charSequence) : ConstantPool.CHAR_ZERO;
	}

	public static <T extends CharSequence> boolean mapIfNonBlank(final T charSequence, ToBoolFunction<T> mapper) {
		return nonBlank(charSequence) ? mapper.applyAsBool(charSequence) : ConstantPool.BOOLEAN_FALSE;
	}

	/* ************************************************ trim ************************************************ */
	public static CharSequence trimStart(CharSequence charSequence) {
		return trim(charSequence, TrimMode.START);
	}

	public static CharSequence trimStart(CharSequence charSequence, Predicate<Character> predicate) {
		return trim(charSequence, TrimMode.START, predicate);
	}

	public static CharSequence trimEnd(CharSequence charSequence) {
		return trim(charSequence, TrimMode.END);
	}

	public static CharSequence trimEnd(CharSequence charSequence, Predicate<Character> predicate) {
		return trim(charSequence, TrimMode.END, predicate);
	}

	public static CharSequence trim(CharSequence charSequence) {
		return trim(charSequence, TrimMode.ALL);
	}

	public static CharSequence trim(CharSequence charSequence, TrimMode mode) {
		return trim(charSequence, mode, Character::isWhitespace);
	}

	public static CharSequence trim(CharSequence charSequence, TrimMode mode, Predicate<Character> predicate) {
		if (isNull(charSequence)) {
			return null;
		}
		int length = charSequence.length();
		int start = 0;
		int end = length;
		// 扫描字符串头部
		if (TrimMode.ALL == mode || mode == TrimMode.START) {
			while ((start < end) && (predicate.test(charSequence.charAt(start)))) {
				start++;
			}
		}
		// 扫描字符串尾部
		if (TrimMode.ALL == mode || mode == TrimMode.END) {
			while ((start < end) && (predicate.test(charSequence.charAt(end - 1)))) {
				end--;
			}
		}
		if ((start > 0) || (end < length)) {
			return sub(charSequence, start, end);
		}
		return charSequence;
	}

	public enum TrimMode {
		ALL, START, END;
	}

	protected CharSequenceUtil() {
		throw new UnsupportedOperationException();
	}
}
