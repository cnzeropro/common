package org.zero.common.api.extra.loki.model.qo;

import lombok.Data;

/**
 * @author zero
 * @since 2023/8/28
 */
@Data
public class LokiQueryQO {
    private String direction = "BACKWARD";
    private Integer limit = 100;
    private String query;
    private Long time;
}
