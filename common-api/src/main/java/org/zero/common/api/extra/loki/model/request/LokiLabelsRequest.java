package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Loki 标签查询请求。
 * <p>
 * 对应 {@code GET /loki/api/v1/labels} 的查询参数。
 *
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
public class LokiLabelsRequest implements Serializable {
    /**
     * {@code start} - 查询开始时间，单位为纳秒级 Unix 时间戳。
     */
    private BigInteger start;
    /**
     * {@code end} - 查询结束时间，单位为纳秒级 Unix 时间戳。
     */
    private BigInteger end;
    /**
     * {@code since} - 相对 {@code end} 反推 {@code start} 的 duration - 持续时间。
     */
    private String since;
    /**
     * {@code query} - 日志流选择器，用于限制需要返回标签名的日志流范围。
     */
    private String query;
}
