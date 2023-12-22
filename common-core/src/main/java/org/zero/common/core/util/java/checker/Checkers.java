package org.zero.common.core.util.java.checker;

import lombok.experimental.UtilityClass;

/**
 * @author Zero
 * @since 2023/4/18
 */
@UtilityClass
public class Checkers {
    public static <T> BeanChecker<T> beanChecker(final T value) {
        return new BeanChecker<>(value);
    }

    public static <T> BeanChecker<T> beanChecker(final T value, String message) {
        return new BeanChecker<>(value, message);
    }
}
