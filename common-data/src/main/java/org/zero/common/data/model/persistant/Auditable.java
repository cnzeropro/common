package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface Auditable<CreatedBy extends Serializable, CreatedAt extends Serializable & Comparable<?>, UpdatedBy extends Serializable, UpdatedAt extends Serializable & Comparable<?>> extends CreationAuditable<CreatedBy, CreatedAt>, UpdateAuditable<UpdatedBy, UpdatedAt> {
}
