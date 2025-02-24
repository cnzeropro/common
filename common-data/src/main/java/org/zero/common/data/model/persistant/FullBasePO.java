package org.zero.common.data.model.persistant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class FullBasePO
        extends BasePO
        implements Auditable<Long, LocalDateTime, Long, LocalDateTime>,
        SoftDeletable<Boolean>,
        Versioned<Long> {
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Long version;
}
