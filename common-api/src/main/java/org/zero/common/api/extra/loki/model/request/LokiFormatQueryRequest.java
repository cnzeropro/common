package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Loki LogQL 格式化请求。
 * <p>
 * 对应 {@code POST /loki/api/v1/format_query} 的表单参数。
 *
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
public class LokiFormatQueryRequest implements Serializable {
    /**
     * {@code query} - 需要格式化的 LogQL 查询语句。
     *
     * @see <a href="https://grafana.com/docs/loki/latest/query/">LogQL</a>
     */
    private String query;
}
