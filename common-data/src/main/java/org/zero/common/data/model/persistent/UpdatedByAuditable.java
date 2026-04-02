package org.zero.common.data.model.persistent;

import java.io.Serializable;

/**
 * 更新人审计接口
 *
 * @param <UpdatedBy> 更新人
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface UpdatedByAuditable<UpdatedBy extends Serializable> {
    /**
     * 获取更新人
     *
     * @return 更新人
     */
    default UpdatedBy getUpdatedBy() {
        return null;
    }

    /**
     * 设置更新人
     *
     * @param updatedBy 更新人
     */
    default void setUpdatedBy(UpdatedBy updatedBy) {
    }
}
