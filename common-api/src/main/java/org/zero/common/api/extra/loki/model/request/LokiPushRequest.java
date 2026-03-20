package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.Accessors;
import org.zero.common.api.extra.loki.model.common.LokiStream;

import java.io.Serializable;
import java.util.Collection;

/**
 * Loki 日志推送请求。
 * <p>
 * 对应 {@code POST /loki/api/v1/push} 的 JSON 请求体。
 *
 * @author zero
 * @since 2023/9/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
public class LokiPushRequest implements Serializable {
    /**
     * {@code streams} - 需要推送到 Loki 的日志流集合。
     */
    @Singular
    private Collection<LokiStream> streams;
}
