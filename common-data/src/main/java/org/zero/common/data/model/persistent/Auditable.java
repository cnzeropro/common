package org.zero.common.data.model.persistent;

import java.io.Serializable;

/**
 * 可审计接口
 *
 * @param <CreatedBy> 创建人
 * @param <CreatedAt> 创建时间
 * @param <UpdatedBy> 更新人
 * @param <UpdatedAt> 更新时间
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface Auditable<CreatedBy extends Serializable, CreatedAt extends Serializable & Comparable<?>, UpdatedBy extends Serializable, UpdatedAt extends Serializable & Comparable<?>> extends CreationAuditable<CreatedBy, CreatedAt>, UpdateAuditable<UpdatedBy, UpdatedAt> {
}
