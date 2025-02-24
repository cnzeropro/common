package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface Versioned<Version extends Serializable & Comparable<?>> {
    default Version getVersion() {
        return null;
    }

    default void setVersion(Version version) {

    }
}
