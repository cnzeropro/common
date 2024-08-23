package org.zero.common.core.xss;

/**
 * @author zero
 * @since 2024/4/30
 */
public enum Mode {
    /**
     * 检查
     */
    CHECK {
        @Override
        public String apply(String value) {
            XssChecker.checkOrElseThrow(value);
            return value;
        }
    },
    /**
     * 转义
     */
    ESCAPE {
        @Override
        public String apply(String value) {
            // cn.hutool.core.util.EscapeUtil.escapeHtml4(value);
            return org.apache.commons.text.StringEscapeUtils.escapeHtml4(value);
        }
    },
    /**
     * 过滤
     */
    FILTER {
        @Override
        public String apply(String value) {
            return cn.hutool.http.HtmlUtil.filter(value);
        }
    },
    ;

    protected abstract String apply(String value);
}
