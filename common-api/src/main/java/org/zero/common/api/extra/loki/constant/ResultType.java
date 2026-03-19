package org.zero.common.api.extra.loki.constant;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/2
 */
public enum ResultType {
    STREAMS,
    MATRIX,
    VECTOR;

    public static ResultType of(String value) {
        for (ResultType resultType : values()) {
            if (resultType.name().equalsIgnoreCase(value)) {
                return resultType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
