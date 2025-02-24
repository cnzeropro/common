package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface UpdateTimeAuditable<UpdatedAt extends Serializable & Comparable<?>> {
    default UpdatedAt getUpdatedAt() {
        return null;
    }

    default void setUpdatedAt(UpdatedAt updatedAt) {
    }
}
