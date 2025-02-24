package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface UpdaterAuditable<UpdatedBy extends Serializable> {
    default UpdatedBy getUpdatedBy() {
        return null;
    }

    default void setUpdatedBy(UpdatedBy updatedBy) {
    }
}
