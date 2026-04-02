package org.zero.common.data.model.persistent;

import java.io.Serializable;

/**
 * 标识（编号）接口
 *
 * @param <Id> 标识（编号）
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface Identifiable<Id extends Serializable> {
    /**
     * 获取标识（编号）
     *
     * @return 标识（编号）
     */
    default Id getId() {
        return null;
    }

    /**
     * 设置标识（编号）
     *
     * @param id 标识（编号）
     */
    default void setId(Id id) {
    }
}
