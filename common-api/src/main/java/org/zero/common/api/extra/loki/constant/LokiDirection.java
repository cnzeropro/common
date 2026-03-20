package org.zero.common.api.extra.loki.constant;

import java.util.Locale;

/**
 * Loki 查询方向枚举。
 * <p>
 * 对应 Grafana Loki HTTP API 中的 {@code direction} 参数，用于控制日志结果的排序方向。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/1
 */
public enum LokiDirection {
    /**
     * {@code forward} - 按时间正序返回结果。
     */
    FORWARD,
    /**
     * {@code backward} - 按时间倒序返回结果。
     */
    BACKWARD;

    @Override
    public String toString() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
