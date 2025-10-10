package org.zero.common.core.util.java.lang;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/29
 */
public class NumberUtil {
    public static double toDouble(final String str, final double defaultValue) {
        if (CharSequenceUtil.isBlank(str)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static float toFloat(final String str, final float defaultValue) {
        if (CharSequenceUtil.isBlank(str)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }
}
