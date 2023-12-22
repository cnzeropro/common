package org.zero.common.api.extra.loki.model;

import lombok.Data;

/**
 * @author zero
 * @since 2023/8/28
 */
@Data
public class LokiResult<T> {
    private String status;
    private T data;

    public static <T> LokiResult<T> error() {
        LokiResult<T> result = new LokiResult<>();
        result.setStatus("fail");
        return result;
    }
}