package org.zero.common.api.extra.loki.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zero.common.api.extra.loki.model.common.LokiStats;
import org.zero.common.api.extra.loki.model.common.LokiStream;
import org.zero.common.api.extra.loki.model.common.LokiVector;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LokiQueryResponse implements Serializable {
    /**
     * 返回数据类型。vector 或 streams
     *
     * @see LokiVector
     * @see LokiStream
     */
    private String resultType;
    /**
     * 查询结果
     */
    private List<Map<String, Object>> result;
    /**
     * 统计信息
     */
    private LokiStats stats;
}
