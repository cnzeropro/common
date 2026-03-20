package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Loki ingester 关闭请求参数。
 * <p>
 * 对应 {@code GET/POST /ingester/shutdown} 的查询参数或表单参数。
 *
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
     * {@code flush} - 关闭前是否先执行 flush - 刷盘。
     */
    @Builder.Default
    private Boolean flush = Boolean.TRUE;
    /**
     * {@code delete_ring_tokens} - 是否删除当前 ingester 对应的 ring token 文件。
     * <p>
     * Java 字段名为 {@code deleteRingTokens}，编码时会转换为官方参数 {@code delete_ring_tokens}。
     */
    @Builder.Default
    private Boolean deleteRingTokens = Boolean.FALSE;
    /**
     * {@code terminate} - 服务关闭后是否终止 Loki 进程。
     */
    @Builder.Default
    private Boolean terminate = Boolean.TRUE;
}
