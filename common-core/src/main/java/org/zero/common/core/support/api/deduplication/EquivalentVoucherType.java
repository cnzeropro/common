package org.zero.common.core.support.api.deduplication;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/26
 */
public enum EquivalentVoucherType {
    NONE,
    AUTO,
    CUSTOM,
    REQUEST_METHOD,
    REQUEST_URI,
    REQUEST_PARAMS,
    REQUEST_BODY,
    REQUEST_HEADERS,
    REQUEST_COOKIES,
    ;
}
