package org.zero.common.core.util.java.lang;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/14
 */
public class CharUtil {
    /**
     * 判断字符是否为中文
     *
     * @param c 字符
     * @return 是否为中文
     */
    public static boolean isChinese(int c) {
        Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(c);
        return unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || unicodeBlock == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B;
    }

    /**
     * 判断字符是否为中文
     *
     * @param c 字符
     * @return 是否为中文
     */
    public static boolean isChinese(char c) {
        return isChinese((int) c);
    }

    protected CharUtil() {
        throw new UnsupportedOperationException();
    }
}
