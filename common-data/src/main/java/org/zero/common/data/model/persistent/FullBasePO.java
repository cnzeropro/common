package org.zero.common.data.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 全功能持久对象基类，在 {@link BasePO} 基础上集成审计、逻辑删除、乐观锁与版本号能力
 * <p>
 * 包含字段：{@code id}、{@code createdBy}、{@code createdAt}、{@code updatedBy}、{@code updatedAt}、
 * {@code deleted}、{@code lock}、{@code version}
 *
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
        OptimisticLockable<Long>,
        Versioned<Long> {
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Long lock;
    private Long version;
}
