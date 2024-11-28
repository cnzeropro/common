package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.Accessors;
import org.zero.common.api.extra.loki.model.common.LokiStream;

import java.io.Serializable;
import java.util.List;

/**
 * @author zero
 * @since 2023/9/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
public class LokiPushRequest implements Serializable {
    @Singular
    private List<LokiStream> streams;
}
