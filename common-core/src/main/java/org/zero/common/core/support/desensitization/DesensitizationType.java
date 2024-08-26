package org.zero.common.core.support.desensitization;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.Getter;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/12
 */
@Getter
public enum DesensitizationType {
    /**
     * 不脱敏
     */
    NONE {
        @Override
        public String desensitize(String value) {
            return value;
        }
    },
    /**
     * 自定义脱敏
     */
    CUSTOM,
    /**
     * 手机号
     * <pre>
     *     {@code 13512342210  ->  135****2210}
     * </pre>
     */
    MOBILE_PHONE(3, -5),
    /**
     * 固定电话（座机号）
     * <pre>
     *     {@code 1087654321  ->  1087****21}
     * </pre>
     */
    FIXED_PHONE(4, -3),
    /**
     * 中文姓名
     * <pre>
     *     {@code 王小明  ->  王**}
     * </pre>
     */
    CHINESE_NAME(1),
    /**
     * 地址
     * <pre>
     *     {@code 北京市朝阳区国家体育场南路1号  ->  北京市朝阳区*********}
     * </pre>
     */
    ADDRESS() {
        /**
         * ”区“后面的字符串脱敏
         */
        @Override
        public String desensitize(String value, String replacement) {
            if (Objects.isNull(value)) {
                return null;
            }
            if (value.contains("区")) {
                int start = value.indexOf("区") + 1;
                int end = value.length() - 1;
                return super.desensitize(value, start, end, replacement);
            }
            return value;
        }
    },
    /**
     * 身份证号
     * <pre>
     *     {@code 51343620000320711X  ->  5***************1x}
     * </pre>
     */
    ID_CARD(1, -3),
    /**
     * 邮箱
     * <pre>
     *     {@code zero@qq.com  ->  z***@qq.com}
     * </pre>
     */
    EMAIL {
        @Override
        public String desensitize(String value, String replacement) {
            if (Objects.isNull(value)) {
                return null;
            }
            if (value.contains("@")) {
                int start = 1;
                int end = value.indexOf("@") - 1;
                return super.desensitize(value, start, end, replacement);
            }
            return value;
        }
    },
    /**
     * 密码
     * <pre>
     *     {@code abc123  ->  ******}
     * </pre>
     */
    PASSWORD,
    /**
     * 车牌号
     * <pre>
     *     {@code 川A12345D  ->  川A1****D}
     * </pre>
     */
    CAR_LICENSE(3, -2),
    /**
     * 银行卡号
     * <pre>
     *     {@code 1234222233334444678  ->  1234 **** **** **** 678}
     * </pre>
     */
    BANK_CARD {
        @Override
        public String desensitize(String value, String replacement) {
            if (Objects.isNull(value)) {
                return null;
            }
            final int length = value.length();
            final int endLength = length % 4 == 0 ? 4 : length % 4;
            final int midLength = length - 4 - endLength;

            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(value, 0, 4);
            for (int i = 0; i < midLength; ++i) {
                if (i % 4 == 0) {
                    stringBuilder.append(' ');
                }
                stringBuilder.append(replacement);
            }
            stringBuilder.append(' ').append(value, length - endLength, length);
            return stringBuilder.toString();
        }
    },
    /**
     * IPv4
     * <pre>
     *     {@code 127.0.0.1  ->  127.*.*.*}
     * </pre>
     */
    IPV4 {
        @Override
        public String desensitize(String value, String replacement) {
            if (Objects.isNull(value)) {
                return null;
            }
            return String.format("%s.%s.%s.%s",
                    CharSequenceUtil.subBefore(value, '.', false),
                    replacement, replacement, replacement);
        }
    },
    /**
     * IPv6
     * <pre>
     *     {@code 2001:0db8:86a3:08d3:1319:8a2e:0370:7344  ->  2001:*:*:*:*:*:*:*}
     * </pre>
     */
    IPV6 {
        @Override
        public String desensitize(String value, String replacement) {
            if (Objects.isNull(value)) {
                return null;
            }
            return String.format("%s:%s:%s:%s:%s:%s:%s:%s",
                    CharSequenceUtil.subBefore(value, ':', false),
                    replacement, replacement, replacement, replacement, replacement, replacement, replacement);
        }
    },
    /**
     * MAC 地址
     * <pre>
     *     {@code B0-A4-60-9A-66-1D  ->  B0-*-*-*-*-*}
     * </pre>
     */
    MAC {
        @Override
        public String desensitize(String value, String replacement) {
            if (Objects.isNull(value)) {
                return null;
            }
            return String.format("%s-%s-%s-%s-%s-%s",
                    CharSequenceUtil.subBefore(value, '-', false),
                    replacement, replacement, replacement, replacement, replacement);
        }
    },
    /**
     * 只显示第一个字符
     * <pre>
     *     {@code 123456  ->  1*****}
     * </pre>
     */
    FIRST_MASK(1),
    /**
     * 清空为 null
     * <pre>
     *     {@code abcdefg  ->  null}
     * </pre>
     */
    CLEAR_TO_NULL {
        @Override
        public String desensitize(String value) {
            return null;
        }
    },
    /**
     * 清空为 ""
     * <pre>
     *     {@code 123abc  ->  }
     * </pre>
     */
    CLEAR_TO_EMPTY {
        @Override
        public String desensitize(String value) {
            return "";
        }
    },
    ;

    /**
     * 脱敏填充字符
     */
    private String replacement = "*";

    /**
     * 脱敏开始位置
     * <p>
     * 小于 0 表示不脱敏
     */
    private int start;

    /**
     * 脱敏结束位置
     * <p>
     * 小于 0 时，-n 表示“脱敏字串长度-n”，如：-1 表示脱敏结束位置到整个字串长度
     */
    private int end = -1;

    /**
     * 脱敏，如果重写以重写方法的规则为主
     */
    public String desensitize(String value) {
        return this.desensitize(value, replacement);
    }

    /**
     * 脱敏，如果重写以重写方法的规则为主
     */
    public String desensitize(String value, String replacement) {
        return this.desensitize(value, start, end, replacement);
    }

    /**
     * 脱敏，如果重写以重写方法的规则为主
     */
    public String desensitize(String value, int start, int end, String replacement) {
        if (Objects.isNull(value)) {
            return null;
        }
        // 如果外部传入的脱敏填充字符为空，则使用内部配置的脱敏填充字符
        if (CharSequenceUtil.isBlank(replacement)) {
            replacement = this.replacement;
        }
        // 如果结束位置小于0，则重新计算结束位置
        if (end < 0) {
            end = value.length() + end;
        }
        // 如果给定的起始位置还小于0，则不脱敏
        if (start < 0) {
            return value;
        }
        return CharSequenceUtil.replace(value, start, end + 1, replacement);
    }

    DesensitizationType() {
    }

    DesensitizationType(int start) {
        this.start = start;
    }

    DesensitizationType(int start, int end) {
        this.start = start;
        this.end = end;
    }

    DesensitizationType(int start, int end, String replacement) {
        this.start = start;
        this.end = end;
        this.replacement = replacement;
    }
}
