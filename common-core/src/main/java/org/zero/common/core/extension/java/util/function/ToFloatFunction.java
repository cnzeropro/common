package org.zero.common.core.extension.java.util.function;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/28
 */
@FunctionalInterface
public interface ToFloatFunction<T> {
    float applyAsFloat(T value);
}
