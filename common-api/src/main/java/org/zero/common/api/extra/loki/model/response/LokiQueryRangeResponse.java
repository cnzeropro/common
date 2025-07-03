package org.zero.common.api.extra.loki.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zero.common.api.extra.loki.constant.ResultType;
import org.zero.common.api.extra.loki.model.common.LokiMatrix;
import org.zero.common.api.extra.loki.model.common.LokiResult;
import org.zero.common.api.extra.loki.model.common.LokiStats;
import org.zero.common.api.extra.loki.model.common.LokiStream;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LokiQueryRangeResponse implements Serializable {
    /**
     * 返回数据类型。matrix 或 streams
     *
     * @see LokiMatrix
     * @see LokiStream
     */
    private ResultType resultType;
    /**
     * 查询结果
     */
    private Collection<? extends LokiResult> result;
    /**
     * 统计信息
     */
    private LokiStats stats;
}
