package org.zero.common.core.extension.api.deduplication;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/26
 */
public enum EquivalentVoucherType {
    NONE,
    REQUEST_METHOD,
    REQUEST_URI,
    REQUEST_PARAMS,
    REQUEST_BODY,
    REQUEST_HEADERS,
    REQUEST_COOKIES,
    CUSTOM,
    ;
}
