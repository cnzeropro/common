package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.Accessors;
import org.zero.common.api.extra.loki.model.common.Value;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
    private List<Stream> streams;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    @Accessors(chain = true)
    public static class Stream implements Serializable {
        private Map<String, Object> stream;
        @Singular
        private List<Value> values;
    }
}
