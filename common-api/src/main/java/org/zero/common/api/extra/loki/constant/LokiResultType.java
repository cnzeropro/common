package org.zero.common.api.extra.loki.constant;

import java.util.Locale;

/**
 * Loki 查询结果类型枚举。
 * <p>
 * 对应 Grafana Loki HTTP API 响应体中的 {@code resultType} 字段，用于指示 {@code data.result} 的结构。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/2
 */
public enum LokiResultType {
    /**
     * {@code streams} - 日志流结果，返回标签集和多条日志行。
     */
    STREAMS,
    /**
     * {@code matrix} - 区间指标结果，返回标签集和多个时间点的指标值。
     */
    MATRIX,
    /**
     * {@code vector} - 瞬时指标结果，返回标签集和单个时间点的指标值。
     */
    VECTOR;

    public static LokiResultType of(String value) {
        for (LokiResultType resultType : values()) {
            if (resultType.name().equalsIgnoreCase(value)) {
                return resultType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
