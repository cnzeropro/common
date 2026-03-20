package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Loki 删除请求创建参数。
 * <p>
 * 对应 {@code POST /loki/api/v1/delete} 与 {@code PUT /loki/api/v1/delete} 的查询参数。
 *
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
public class LokiDeleteRequest implements Serializable {
    /**
     * {@code query} - 需要删除的日志匹配条件。
     *
     * @see <a href="https://grafana.com/docs/loki/latest/query/">LogQL</a>
     */
    private String query;
    /**
     * {@code start} - 删除时间范围的开始时间。
     * <p>
     * 支持 Unix 时间戳或 {@code RFC3339}/{@code RFC3339Nano} 格式；该参数为必填项。
     */
    private String start;
    /**
     * {@code end} - 删除时间范围的结束时间。
     * <p>
     * 支持 Unix 时间戳或 {@code RFC3339}/{@code RFC3339Nano} 格式；为空时由服务端使用当前时间。
     */
    private String end;
    /**
     * {@code max_interval} - 单次删除请求允许跨越的最大时间段。
     * <p>
     * Java 字段名为 {@code maxInterval}，编码时会转换为官方参数 {@code max_interval}。
     */
    private String maxInterval;
}
