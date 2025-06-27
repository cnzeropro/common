package org.zero.common.test.feature.bean.obtain;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.zero.common.core.support.context.spring.SpringUtils;

/**
 * {@link BeanFactoryUtils} 是 Spring 自带的工具类，另外还有{@link org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils}
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/26
 */
@Component
class UtilWay {
    Environment environment = BeanFactoryUtils.beanOfType(SpringUtils.getConfigurableListableBeanFactory(), Environment.class);
}
