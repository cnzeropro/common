package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.zero.common.api.extra.loki.constant.LokiDirection;

/**
 * Loki 区间查询请求。
 * <p>
 * 对应 {@code GET /loki/api/v1/query_range} 的查询参数。
 *
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
public class LokiQueryRangeRequest {
    /**
     * {@code direction} - 日志返回顺序。
     * <p>
     * 可选值为 {@code backward}、{@code forward}，默认值为 {@code backward}。
     */
    @Builder.Default
    private LokiDirection direction = LokiDirection.BACKWARD;
    /**
     * {@code limit} - 返回的最大日志条数。
     */
    @Builder.Default
    private Integer limit = 100;
    /**
     * {@code query} - 要执行的 LogQL 查询语句。
     *
     * @see <a href="https://grafana.com/docs/loki/latest/query/">LogQL</a>
     */
    private String query;
    /**
     * {@code start} - 查询开始时间。
     * <p>
     * 支持纳秒级 Unix 时间戳、浮点秒时间戳、{@code RFC3339}、{@code RFC3339Nano}。
     */
    private String start;
    /**
     * {@code end} - 查询结束时间。
     * <p>
     * 支持纳秒级 Unix 时间戳、浮点秒时间戳、{@code RFC3339}、{@code RFC3339Nano}。
     */
    private String end;
    /**
     * {@code since} - 相对 {@code end} 反推 {@code start} 的 duration - 持续时间。
     */
    private String since;
    /**
     * {@code step} - 指标查询步长。
     * <p>
     * 支持 Prometheus duration 字符串或浮点秒字符串。
     */
    private String step;
    /**
     * {@code interval} - 日志流采样间隔。
     * <p>
     * 支持 duration 字符串或浮点秒字符串。
     */
    private String interval;
}
