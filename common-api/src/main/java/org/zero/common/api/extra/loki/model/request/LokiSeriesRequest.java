package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Loki Series 查询请求。
 * <p>
 * 对应 {@code GET/POST /loki/api/v1/series} 的查询参数或表单参数。
 *
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
public class LokiSeriesRequest implements Serializable {
    /**
     * {@code match[]} - 重复出现的日志流选择器参数。
     * <p>
     * Java 字段名为 {@code match}，编码时会转换为官方要求的 {@code match[]}。
     */
    private String[] match;
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
}
