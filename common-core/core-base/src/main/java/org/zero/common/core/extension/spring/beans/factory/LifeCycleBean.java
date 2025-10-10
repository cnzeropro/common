package org.zero.common.core.extension.spring.beans.factory;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.zero.common.core.extension.java.LifeCycle;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/14
 */
public interface LifeCycleBean extends LifeCycle, InitializingBean, DisposableBean {
    @Override
    default void afterPropertiesSet() throws Exception {
        initialize();
    }

    @Override
    default void initialize() throws Exception {
    }

    @Override
    default void destroy() throws Exception {
    }
}
