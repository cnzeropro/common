package org.zero.common.test.feature.bean.obtain;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * {@link ObjectProvider} 需要配合其他注入方式使用，可以方便地解决循环依赖或可选依赖问题，更安全的获取 bean
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/26
 */
@Component
class ObjectProviderWay {
    final Environment environment;

    ObjectProviderWay(ObjectProvider<Environment> environmentObjectProvider) {
        this.environment = environmentObjectProvider.getIfAvailable();
    }
}
