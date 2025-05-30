package org.zero.common.test.feature.annotation.obscure;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareParents;
import org.springframework.stereotype.Component;

/**
 * {@linkplain DeclareParents @DeclareParents} 用于声明匹配的类型拥有一个新的父类型（自动实现一个指定的父类，可做 Mixin）
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/26
 */
@Aspect
@Component
class DeclareParentsAnnotation {
    /**
     * value 值是 Aspectj 切点表达式，因此可以支持多种写法配置。
     * 如：{@code org.zero.*+}（匹配 org.zero 包及其子包下的所有类）
     */
    @DeclareParents(value = "org.zero.common.test.feature.annotation.obscure.DeclareParentsAnnotation.CustomService", defaultImpl = ServiceImpl.class)
    Service service;

    interface Service {
        Object service();
    }

    static class ServiceImpl implements Service {
        @Override
        public Object service() {
            return "ServiceImpl.doSomething";
        }
    }

    @Component
    static class CustomService {
    }
}
