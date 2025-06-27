package org.zero.common.test.feature.bean.obtain;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * {@linkplain Inject @Inject} 注解是 JSR-330 标准注解，同时该注解还可以与 {@linkplain javax.inject.Named @Named} 一起使用来限定注入的 bean
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/26
 */
@Component
class InjectAnnotation {
    @Inject
    Environment environment;
}
