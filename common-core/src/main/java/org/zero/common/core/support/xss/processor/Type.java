package org.zero.common.core.support.xss.processor;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/10
 */
public enum Type {
    URI,
    URL,
    HEADER,
    PARAM,
    QUERY_STRING,
    BODY_INPUT_STREAM,
    BODY_READER,
    COOKIE
    ;
}
