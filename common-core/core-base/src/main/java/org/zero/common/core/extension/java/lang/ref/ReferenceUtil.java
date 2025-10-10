package org.zero.common.core.extension.java.lang.ref;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/3
 */
public class ReferenceUtil {
    /**
     * 获得引用
     *
     * @param <T>      被引用对象类型
     * @param type     引用类型枚举
     * @param referent 被引用对象
     * @return {@link Reference}
     */
    public static <T> Reference<T> create(ReferenceType type, T referent) {
        return create(type, referent, null);
    }

    /**
     * 获得引用
     *
     * @param <T>      被引用对象类型
     * @param type     引用类型枚举
     * @param referent 被引用对象
     * @param queue    引用队列
     * @return {@link Reference}
     */
    public static <T> Reference<T> create(ReferenceType type, T referent, ReferenceQueue<T> queue) {
        switch (type) {
            case SOFT:
                return new SoftReference<>(referent, queue);
            case WEAK:
                return new WeakReference<>(referent, queue);
            case PHANTOM:
                return new PhantomReference<>(referent, queue);
            default:
                return null;
        }
    }
}
