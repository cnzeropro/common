package org.zero.common.data.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * sql 条件
 *
 * @author zero
 * @since 2024/6/20
 */
@Data
@With
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class Condition implements Serializable {
    /**
     * 条件字段
     */
    @NotEmpty
    private String field;
    /**
     * 操作符
     */
    private Operator operator;
    /**
     * 条件值
     */
    private Object value;
}
