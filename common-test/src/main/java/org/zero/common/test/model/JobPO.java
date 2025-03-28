package org.zero.common.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/26
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class JobPO {
    private Long id;
    private String name;
    private String description;
    private String cron;
}
