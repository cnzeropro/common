package org.zero.common.api.extra.loki.model.qo;

import lombok.Data;

/**
 * @author zero
 * @since 2023/8/28
 */
@Data
public class LokiLabelsQO {
    private Long start;
    private Long end;
    private Long since;
    private String query;
}
