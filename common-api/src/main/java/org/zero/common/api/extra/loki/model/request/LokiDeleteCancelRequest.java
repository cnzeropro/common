package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Loki 删除请求取消参数。
 * <p>
 * 对应 {@code DELETE /loki/api/v1/delete} 的查询参数，用于取消指定的删除任务。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
public class LokiDeleteCancelRequest implements Serializable {
    /**
     * {@code request_id} - 需要取消的删除任务 ID。
     */
    private String requestId;
    /**
     * {@code force} - 是否强制取消删除任务。
     * <p>
     * 为空时使用服务端默认行为。
     */
    private Boolean force;
}
