package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Loki 日志级别修改请求。
 * <p>
 * 对应 {@code POST /log_level} 的表单参数。
 *
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
public class LokiLogLevelRequest implements Serializable {
    /**
     * {@code log_level} - 需要切换到的日志级别。
     * <p>
     * Java 字段名为 {@code logLevel}，编码时会转换为官方参数 {@code log_level}。
     */
    private String logLevel;
}
