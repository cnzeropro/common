package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface CreationTimeAuditable<CreatedAt extends Serializable & Comparable<?>> {
    default CreatedAt getCreatedAt() {
        return null;
    }

    default void setCreatedAt(CreatedAt createdAt) {
    }
}
