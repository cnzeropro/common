package org.zero.common.test.feature.bean.obtain;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/26
 */
@Component
class ObjectFactoryWay {
    final Environment environment;

    ObjectFactoryWay(ObjectFactory<Environment> environmentObjectFactory) {
        this.environment = environmentObjectFactory.getObject();
    }
}
