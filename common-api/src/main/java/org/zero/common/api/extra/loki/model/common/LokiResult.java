package org.zero.common.api.extra.loki.model.common;

import java.io.Serializable;

/**
 * Loki 查询结果基类。
 * <p>
 * 对应 Grafana Loki HTTP API 中 {@code data.result} 数组里的单个结果项。
 * 子类会根据 {@code resultType} 表示日志流、瞬时指标或区间指标。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/27
 */
public abstract class LokiResult implements Serializable {
}
