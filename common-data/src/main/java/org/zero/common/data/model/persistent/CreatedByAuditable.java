package org.zero.common.data.model.persistent;

import java.io.Serializable;

/**
 * 创建人审计接口
 *
 * @param <CreatedBy> 创建人
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface CreatedByAuditable<CreatedBy extends Serializable> {
    /**
     * 获取创建人
     *
     * @return 创建人
     */
    default CreatedBy getCreatedBy() {
        return null;
    }

    /**
     * 设置创建人
     *
     * @param createdBy 创建人
     */
    default void setCreatedBy(CreatedBy createdBy) {
    }
}
