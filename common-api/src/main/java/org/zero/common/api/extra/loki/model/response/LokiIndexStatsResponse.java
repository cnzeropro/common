package org.zero.common.api.extra.loki.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Loki 索引统计响应。
 * <p>
 * 对应 {@code /loki/api/v1/index/stats} 的响应体。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LokiIndexStatsResponse implements Serializable {
    /**
     * {@code streams} - 命中的日志流数量。
     */
    private Integer streams;
    /**
     * {@code chunks} - 命中的 chunk 数量。
     */
    private Integer chunks;
    /**
     * {@code entries} - 命中的日志条目数量。
     */
    private Integer entries;
    /**
     * {@code bytes} - 命中的总字节数。
     */
    private Long bytes;
}
