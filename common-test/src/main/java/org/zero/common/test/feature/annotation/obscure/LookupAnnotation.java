package org.zero.common.test.feature.annotation.obscure;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * {@linkplain Lookup @Lookup} 根据当前方法的返回值或者该注解配置的 value 属性值（beanName），在容器中查找对应的 bean，通过 {@code BeanFactory#getBean} 进行备案的查找
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/26
 */
@Component
class LookupAnnotation {
    @Component
    @Scope("prototype")
    static class Bean {
    }

    @Component
    abstract static class BusinessBean {
        @Lookup
        public abstract Bean getBean();
    }
}
