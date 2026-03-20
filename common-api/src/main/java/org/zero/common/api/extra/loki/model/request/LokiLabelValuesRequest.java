package org.zero.common.api.extra.loki.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Loki 标签值查询请求。
 * <p>
 * 对应 {@code GET /loki/api/v1/label/{name}/values} 的查询参数，
 * 结构与 {@link LokiLabelsRequest} 一致。
 *
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LokiLabelValuesRequest extends LokiLabelsRequest {
}
