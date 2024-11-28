package org.zero.common.api.extra.loki.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LokiStats implements Serializable {
    private Map<String, Object> summary;
    private Map<String, Object> querier;
    private Map<String, Object> ingester;
}