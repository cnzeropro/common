package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * 逻辑删除接口
 *
 * @param <Deleted> 逻辑删除
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface SoftDeletable<Deleted extends Serializable> {
    /**
     * 获取逻辑删除
     *
     * @return 逻辑删除
     */
    default Deleted getDeleted() {
        return null;
    }

    /**
     * 设置逻辑删除
     *
     * @param deleted 逻辑删除
     */
    default void setDeleted(Deleted deleted) {
    }
}
