package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
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
     * LogQL 查询语句。
     *
     * @see <a href="https://grafana.com/docs/loki/latest/query/">LogQL</a>
     */
    private String query;
    /**
     * 开始时间。Unix 纪元秒值。此参数是必需的。
     */
    private String start;
    /**
     * 结束时间。Unix 纪元秒值或者 RFC 3339 日期时间格式值。如果未指定，默认使用当前时间。
     */
    private String end;
    /**
     * max_interval
     * <p>
     * 删除请求可以跨越的最大时间段。有效时间单位是 s、m 和 h。
     */
    private String maxInterval;
}
