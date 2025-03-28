package org.zero.common.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/26
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskPO {
    private Long id;
    private Long jobId;
    private Double status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
