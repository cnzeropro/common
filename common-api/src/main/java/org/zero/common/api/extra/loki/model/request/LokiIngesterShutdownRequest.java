package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class LokiIngesterShutdownRequest implements Serializable {
    /**
     * 是否刷新数据
     */
    @Builder.Default
    private Boolean flush = Boolean.TRUE;
    /**
     * delete_ring_tokens
     * <p>
     * 如果指定了 {@code -ingester.token-file-path}，是否删除包含该实例 ingester 的 ring_tokens 的文件
     */
    @Builder.Default
    private Boolean deleteRingTokens = Boolean.FALSE;
    /**
     * 在服务关闭后是否终止 Loki 进程
     */
    @Builder.Default
    private Boolean terminate = Boolean.TRUE;
}
