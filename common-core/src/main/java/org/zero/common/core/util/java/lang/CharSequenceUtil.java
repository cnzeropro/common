package org.zero.common.core.util.java.lang;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/1
 */
public class CharSequenceUtil {
    public static boolean isNull(CharSequence charSequence) {
        return ObjectUtil.isNull(charSequence);
    }

    public static boolean isNotNull(CharSequence charSequence) {
        return ObjectUtil.nonNull(charSequence);
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return isNull(charSequence) || charSequence.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence charSequence) {
        return !isEmpty(charSequence);
    }

    public static boolean isBlank(CharSequence charSequence) {
        if (isNotEmpty(charSequence)) {
            int length = charSequence.length();
            for (int i = 0; i < length; i++) {
                if (!Character.isWhitespace(charSequence.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence charSequence) {
        return !isBlank(charSequence);
    }

    public static <T extends CharSequence> T defaultIfBlank(final T charSequence, final T defaultCharSequence) {
        return isBlank(charSequence) ? defaultCharSequence : charSequence;
    }

    protected CharSequenceUtil() {
        throw new UnsupportedOperationException();
    }
}
