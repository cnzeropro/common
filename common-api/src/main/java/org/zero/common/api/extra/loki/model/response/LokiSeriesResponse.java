package org.zero.common.api.extra.loki.model.response;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Loki Series 响应项。
 * <p>
 * 对应 {@code /loki/api/v1/series} 返回数组中的单个标签集合对象。
 * Loki 的标签键是动态的，因此该模型使用 {@code Map<String, String>} 直接承载所有标签。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/30
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LokiSeriesResponse extends LinkedHashMap<String, String> implements Serializable {
    /**
     * 使用已有标签集合构造响应对象。
     *
     * @param labels 动态标签集合
     */
    public LokiSeriesResponse(Map<String, String> labels) {
        super(labels);
    }
}
