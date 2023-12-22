package org.zero.common.api.extra.loki.model.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zero
 * @since 2023/8/28
 */
@Data
public class LokiQueryVO {
    private String resultType;
    private List<Map<String, Object>> result;
    private Stats stats;

    @Data
    public static class Stats {
        private Map<String, Object> summary;
        private Map<String, Object> querier;
        private Map<String, Object> ingester;
    }
}
