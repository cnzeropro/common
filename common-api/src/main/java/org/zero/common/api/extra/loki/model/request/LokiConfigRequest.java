package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.zero.common.api.extra.loki.constant.LokiConfigMode;

import java.io.Serializable;

/**
 * Loki 配置查询请求。
 * <p>
 * 对应 {@code GET /config} 的查询参数，用于控制配置输出模式。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
public class LokiConfigRequest implements Serializable {
    /**
     * {@code mode} - 配置输出模式。
     * <p>
     * 可选值为 {@code diffs}、{@code defaults}；为空时返回默认展示结果。
     */
    private LokiConfigMode mode;
}
