package org.zero.common.test.feature.bean.obtain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * {@linkplain Autowired @Autowired} 注解是 Spring 提供的一种依赖注入方式，可配合 {@linkplain org.springframework.beans.factory.annotation.Qualifier @Qualifier} 一起使用来限定注入的 bean
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/26
 */
@Component
 class AutowiredAnnotation {
    @Autowired
    Environment environment;
}
