package org.zero.common.test.feature.bean.obtain;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/26
 */
@Component
class BeanFactoryWay implements BeanFactoryAware {
    Environment environment;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        environment = beanFactory.getBean(Environment.class);
    }
}
