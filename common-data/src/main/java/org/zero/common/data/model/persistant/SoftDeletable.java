package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface SoftDeletable<Deleted extends Serializable> {
    default Deleted getDeleted() {
        return null;
    }

    default void setDeleted(Deleted deleted) {
    }
}
