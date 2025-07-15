package org.zero.common.core.extension.java;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/14
 */
public interface LifeCycle {
    /**
     * 初始化
     *
     * @throws Exception
     */
    void init() throws Exception;

    /**
     * 销毁
     *
     * @throws Exception
     */
    void destroy() throws Exception;
}
