package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.zero.common.api.extra.loki.constant.Direction;

import java.math.BigInteger;

/**
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
     * 日志排序顺序。可选：backward, forward。默认：backward
     */
    @Builder.Default
    private Direction direction = Direction.BACKWARD;
    /**
     * 查询日志条数。默认：100
     */
    @Builder.Default
    private Integer limit = 100;
    /**
     * 要执行的 LogQL 查询。
     *
     * @see <a href="https://grafana.com/docs/loki/latest/query/">LogQL</a>
     */
    private String query;
    /**
     * 查询的开始时间。Unix 纪元纳秒值。默认：1小时前
     */
    private BigInteger start;
    /**
     * 查询的结束时间。Unix 纪元纳秒值。默认：当前时间
     */
    private BigInteger end;
    /**
     * 用于计算开始时间（start）相对于结束时间（end）的持续时间
     */
    private BigInteger since;
    /**
     * 查询的步长
     */
    private Integer step;
    /**
     * 查询的间隔
     */
    private Integer interval;
}
