package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * 版本接口
 *
 * @param <Version> 版本
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface Versioned<Version extends Serializable & Comparable<?>> {
    /**
     * 获取版本
     *
     * @return 版本
     */
    default Version getVersion() {
        return null;
    }

    /**
     * 设置版本
     *
     * @param version 版本
     */
    default void setVersion(Version version) {
    }
}
