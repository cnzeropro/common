package org.zero.common.api.extra.loki.constant;

import java.util.Locale;

/**
 * Loki 配置查看模式枚举。
 * <p>
 * 对应 {@code GET /config} 的可选查询参数 {@code mode}，用于控制返回配置的展示方式。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/19
 */
public enum LokiConfigMode {
    /**
     * {@code diffs} - 仅返回与默认值不同的配置项。
     */
    DIFFS,
    /**
     * {@code defaults} - 返回带默认值说明的配置视图。
     */
    DEFAULTS;

    @Override
    public String toString() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
