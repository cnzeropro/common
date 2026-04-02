package org.zero.common.data.model.persistent;

import java.io.Serializable;

/**
 * 更新时间审计接口
 *
 * @param <UpdatedAt> 更新时间
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface UpdatedAtAuditable<UpdatedAt extends Serializable & Comparable<?>> {
    /**
     * 获取更新时间
     *
     * @return 更新时间
     */
    default UpdatedAt getUpdatedAt() {
        return null;
    }

    /**
     * 设置更新时间
     *
     * @param updatedAt 更新时间
     */
    default void setUpdatedAt(UpdatedAt updatedAt) {
    }
}
