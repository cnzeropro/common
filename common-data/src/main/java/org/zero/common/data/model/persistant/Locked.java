package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * 乐观锁接口
 *
 * @param <Lock> 乐观锁
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface Locked<Lock extends Serializable> {
    /**
     * 获取乐观锁
     *
     * @return 乐观锁
     */
    default Lock getLock() {
        return null;
    }

    /**
     * 设置乐观锁
     *
     * @param lock 乐观锁
     */
    default void setLock(Lock lock) {
    }
}
