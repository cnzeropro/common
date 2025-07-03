package org.zero.common.api.extra.loki.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/2
 */
@Getter
@RequiredArgsConstructor
public enum ResultType {
    STREAMS("streams"),
    MATRIX("matrix"),
    VECTOR("vector");

    private final String value;

    public static ResultType of(String value) {
        for (ResultType resultType : values()) {
            if (resultType.value.equalsIgnoreCase(value)) {
                return resultType;
            }
        }
        return null;
    }
}
