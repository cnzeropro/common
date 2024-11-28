package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
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
public class LokiLabelsRequest implements Serializable {
    /**
     * 查询的开始时间戳。单位为纳秒。默认：6小时前
     */
    private Long start;
    /**
     * 查询的结束时间戳。单位为纳秒。默认：当前时间
     */
    private Long end;
    /**
     * 用于计算开始时间（start）相对于结束时间（end）的持续时间
     */
    private Long since;
    /**
     * 日志流选择器，用于选择要匹配的流并返回标签名称
     */
    private String query;
}
