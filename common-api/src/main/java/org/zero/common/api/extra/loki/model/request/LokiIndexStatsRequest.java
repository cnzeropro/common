package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Loki 索引统计查询请求。
 * <p>
 * 对应 {@code GET/POST /loki/api/v1/index/stats} 的查询参数或表单参数。
 *
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
public class LokiIndexStatsRequest implements Serializable {
    /**
     * {@code query} - 用于过滤统计范围的 LogQL 语句。
     *
     * @see <a href="https://grafana.com/docs/loki/latest/query/">LogQL</a>
     */
    private String query;
    /**
     * {@code start} - 统计开始时间，单位为纳秒级 Unix 时间戳。
     */
    private BigInteger start;
    /**
     * {@code end} - 统计结束时间，单位为纳秒级 Unix 时间戳。
     */
    private BigInteger end;
}
