package org.zero.common.data.model.persistant;

import java.io.Serializable;

/**
 * 乐观锁接口
 * <p>
 * 大部分情况下，乐观锁的实现方式都是基于版本控制的，因此可直接使用 {@link Versionable} 接口
 *
 * @param <Lock> 乐观锁
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/13
 */
public interface OptimisticLockable<Lock extends Serializable> {
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
