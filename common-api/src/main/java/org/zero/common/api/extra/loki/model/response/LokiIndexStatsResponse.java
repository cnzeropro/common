package org.zero.common.api.extra.loki.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LokiIndexStatsResponse implements Serializable {
    private Integer streams;
    private Integer chunks;
    private Integer entries;
    private Long bytes;
}
