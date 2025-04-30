package org.zero.common.core.extension.java.lang;

import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * 通用构建者
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/29
 */
public abstract class BaseBuilder<B extends BaseBuilder<B, A>, A> {
    @SuppressWarnings("unchecked")
    protected A instance() {
        Class<?> clazz = this.getClass();
        Class<?> enclosingClass = clazz.getEnclosingClass();
        if (Objects.isNull(enclosingClass)) {
            throw new RuntimeException(String.format("%s must have enclosing class", clazz));
        }
        try {
            Constructor<?> constructor = enclosingClass.getConstructor();
            return (A) constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format("%s must have no argument constructor", enclosingClass), e);
        } catch (ClassCastException e) {
            throw new RuntimeException(String.format("%s must be a class", enclosingClass), e);
        } catch (Exception e) {
            throw new RuntimeException("Instantiation failed", e);
        }
    }

    protected void validate(A obj) {
    }

    public A build() {
        A obj = this.instance();
        this.validate(obj);
        return obj;
    }
}
