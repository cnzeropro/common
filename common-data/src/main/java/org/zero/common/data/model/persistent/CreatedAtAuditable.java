package org.zero.common.data.model.persistent;

import java.io.Serializable;

/**
 * 创建时间审计接口
 *
 * @param <CreatedAt> 创建时间
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface CreatedAtAuditable<CreatedAt extends Serializable & Comparable<?>> {
    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    default CreatedAt getCreatedAt() {
        return null;
    }

    /**
     * 设置创建时间
     *
     * @param createdAt 创建时间
     */
    default void setCreatedAt(CreatedAt createdAt) {
    }
}
