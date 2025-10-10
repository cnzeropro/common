package org.zero.common.core.support.converter;

import java.lang.reflect.Type;
import java.util.Deque;
import java.util.function.Supplier;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/24
 */
public abstract class ToDeque<T> extends ToCollection<T> {
    protected ToDeque(Supplier<? extends Deque<T>> dequeSupplier, Type componentType, ConverterComposite converterComposite) {
        super(dequeSupplier, componentType, converterComposite);
    }

    @Override
    public Deque<T> convert(Object source) {
        return (Deque<T>) super.convert(source);
    }
}
