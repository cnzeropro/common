package org.zero.common.data.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 持久对象基类，提供唯一标识（{@code id}）能力
 * <p>
 * 子类可按需组合实现 {@link Auditable}、{@link SoftDeletable}、{@link OptimisticLockable}、{@link Versioned} 等接口，
 * 或直接继承 {@link FullBasePO} 获得全部审计与软删除能力。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class BasePO implements Identifiable<Long>, Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
}
