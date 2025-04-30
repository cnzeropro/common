package org.zero.common.core.support.xss.processor;

/**
 * @author zero
 * @since 2024/4/30
 */
public enum XssMode implements XssProcessor {
    /**
     * 检查
     */
    CHECK {
        @Override
        public String process(String value, UnitType unitType) {
            XssChecker.checkOrElseThrow(value);
            return value;
        }
    },
    /**
     * 转义
     */
    ESCAPE {
        @Override
        public String process(String value, UnitType unitType) {
            // cn.hutool.core.util.EscapeUtil.escapeHtml4(value);
            return org.apache.commons.text.StringEscapeUtils.escapeHtml4(value);
        }
    },
    /**
     * 过滤
     */
    FILTER {
        @Override
        public String process(String value, UnitType unitType) {
            return cn.hutool.http.HtmlUtil.filter(value);
        }
    },
    ;
}
