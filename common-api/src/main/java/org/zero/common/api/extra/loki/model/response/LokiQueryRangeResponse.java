package org.zero.common.api.extra.loki.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zero.common.api.extra.loki.constant.LokiResultType;
import org.zero.common.api.extra.loki.model.common.LokiMatrix;
import org.zero.common.api.extra.loki.model.common.LokiResult;
import org.zero.common.api.extra.loki.model.common.LokiStats;
import org.zero.common.api.extra.loki.model.common.LokiStream;

import java.io.Serializable;
import java.util.Collection;

/**
 * Loki 区间查询响应数据。
 * <p>
 * 对应 {@code /loki/api/v1/query_range} 响应中 {@code data} 节点的内容。
 *
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LokiQueryRangeResponse implements Serializable {
    /**
     * {@code resultType} - 查询结果类型。
     * <p>
     * 支持 {@code matrix}、{@code streams}。
     *
     * @see LokiMatrix
     * @see LokiStream
     */
    private LokiResultType resultType;
    /**
     * {@code result} - 查询结果数组。
     */
    private Collection<? extends LokiResult> result;
    /**
     * {@code stats} - 查询统计信息。
     */
    private LokiStats stats;
}
