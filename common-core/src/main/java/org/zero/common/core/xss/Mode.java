package org.zero.common.core.xss;

import cn.hutool.http.HtmlUtil;
import org.apache.commons.text.StringEscapeUtils;

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
            throw new UnsupportedOperationException();
        }
    },
    /**
     * 转义
     */
    ESCAPE {
        @Override
        public String apply(String value) {
            // return EscapeUtil.escapeHtml4(value);
            return StringEscapeUtils.escapeHtml4(value);
        }
    },
    /**
     * 过滤
     */
    FILTER {
        @Override
        public String apply(String value) {
            return HtmlUtil.filter(value);
        }
    },
    ;

    protected abstract String apply(String value);
}
