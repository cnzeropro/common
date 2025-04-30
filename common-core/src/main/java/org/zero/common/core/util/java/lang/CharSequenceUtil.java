package org.zero.common.core.util.java.lang;

import org.zero.common.data.constant.StringPool;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/1
 */
public class CharSequenceUtil {
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

    public static <T extends CharSequence> T defaultIfNull(final T charSequence, final T defaultCharSequence) {
        return isNull(charSequence) ? defaultCharSequence : charSequence;
    }

    public static <T extends CharSequence> T defaultIfEmpty(final T charSequence, final T defaultCharSequence) {
        return isEmpty(charSequence) ? defaultCharSequence : charSequence;
    }

    public static <T extends CharSequence> T defaultIfBlank(final T charSequence, final T defaultCharSequence) {
        return isBlank(charSequence) ? defaultCharSequence : charSequence;
    }

    public static <T extends CharSequence, R> R mapIfNonNull(final T charSequence, Function<T, R> mapper) {
        return nonNull(charSequence) ? mapper.apply(charSequence) : null;
    }

    public static <T extends CharSequence, R> R mapIfNonEmpty(final T charSequence, Function<T, R> mapper) {
        return nonEmpty(charSequence) ? mapper.apply(charSequence) : null;
    }

    public static <T extends CharSequence, R> R mapIfNonBlank(final T charSequence, Function<T, R> mapper) {
        return nonBlank(charSequence) ? mapper.apply(charSequence) : null;
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
