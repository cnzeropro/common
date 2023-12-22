package org.zero.common.core.util.java.collector;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/1/9
 */
@FunctionalInterface
public interface ToBigDecimalFunction<T> extends Function<T, BigDecimal> {
    @Override
    BigDecimal apply(T value);
}
