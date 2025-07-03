package org.zero.common.api.extra.loki.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author zero
 * @since 2023/8/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
public class LokiSeriesRequest implements Serializable {
    /**
     * 重复的日志流选择器参数，用于选择要返回的流。至少提供一个
     */
    private String[] match;
    /**
     * 开始时间。Unix 纪元纳秒值。
     */
    private BigInteger start;
    /**
     * 结束时间。Unix 纪元纳秒值。
     */
    private BigInteger end;
    /**
     * 用于计算开始时间（start）相对于结束时间（end）的持续时间
     */
    private BigInteger since;
}
