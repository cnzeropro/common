package org.zero.common.api.extra.loki.constant;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/1
 */
public enum Direction {
    FORWARD,
    BACKWARD;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
