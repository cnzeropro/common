package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface CreatorAuditable<CreatedBy extends Serializable> {
    default CreatedBy getCreatedBy() {
        return null;
    }

    default void setCreatedBy(CreatedBy createdBy) {
    }
}
