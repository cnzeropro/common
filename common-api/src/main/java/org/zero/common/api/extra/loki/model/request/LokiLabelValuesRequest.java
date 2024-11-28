package org.zero.common.api.extra.loki.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
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
