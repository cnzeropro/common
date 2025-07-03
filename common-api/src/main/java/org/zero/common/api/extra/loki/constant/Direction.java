package org.zero.common.api.extra.loki.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/1
 */
@Getter
@RequiredArgsConstructor
public enum Direction {
    FORWARD("forward"),
    BACKWARD("backward");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
