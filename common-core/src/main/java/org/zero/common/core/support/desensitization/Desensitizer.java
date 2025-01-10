package org.zero.common.core.support.desensitization;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/1/8
 */
public interface Desensitizer {
    String DEFAULT_REPLACEMENT = "*";

    /**
     * 脱敏
     * <p>
     * <b>注意：默认使用 {@link #DEFAULT_REPLACEMENT} 将源字符串全部脱敏</b>
     *
     * @param value 源字符串
     * @return 脱敏字符串
     */
    default String desensitize(String value) {
        return this.desensitize(value, DEFAULT_REPLACEMENT);
    }

    /**
     * 脱敏
     * <p>
     * <b>注意：默认源字符串全部脱敏</b>
     *
     * @param value       源字符串
     * @param replacement 脱敏填充字符
     * @return 脱敏字符串
     */
    default String desensitize(String value, String replacement) {
        return this.desensitize(value, 0, value.length(), replacement);
    }

    /**
     * 脱敏
     *
     * @param value       源字符串
     * @param start       脱敏起始位置（包含）。从 0 开始，小于 0 时表示不脱敏
     * @param end         脱敏结束位置（包含）。可以为 0。小于 0 时，-n 表示：脱敏字串长度-n，如：-1 表示脱敏结束位置到整个字串长度
     * @param replacement 脱敏填充字符
     * @return 脱敏字符串
     */
    default String desensitize(String value, int start, int end, String replacement) {
        if (Objects.isNull(value)) {
            return null;
        }
        // 如果外部传入的脱敏填充字符为 null，则使用默认脱敏填充字符
        if (Objects.isNull(replacement)) {
            replacement = DEFAULT_REPLACEMENT;
        }
        // 如果结束位置小于 0，则重新计算结束位置
        if (end < 0) {
            end = value.length() + end;
        }
        // 如果给定的起始位置小于 0，则不脱敏
        if (start < 0) {
            return value;
        }

        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i >= start && i <= end) {
                stringBuilder.append(replacement);
            } else {
                stringBuilder.append(chars[i]);
            }
        }
        return stringBuilder.toString();
    }
}
