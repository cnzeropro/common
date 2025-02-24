package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface Identifiable<Id extends Serializable> {
    default Id getId() {
        return null;
    }

    default void setId(Id id) {
    }
}
