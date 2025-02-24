package org.zero.common.data.model.persistant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/14
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProductPO extends FullBasePO {
    private String name;
    private String description;
    private BigDecimal price;
    private Long inventory;
}
