package org.zero.common.api.extra.loki.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Loki 日志级别接口响应。
 * <p>
 * 对应 {@code /log_level} 接口返回的消息体。
 *
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LokiLogLevelResponse implements Serializable {
    /**
     * {@code message} - 日志级别查询或修改结果消息。
     */
    private String message;
}
