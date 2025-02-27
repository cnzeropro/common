package org.zero.common.core.extension.java.converter;

import java.lang.reflect.Type;
import java.util.Queue;
import java.util.function.Supplier;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/24
 */
public abstract class ToQueue<T> extends ToCollection<T> {
    protected ToQueue(Supplier<? extends Queue<T>> queueSupplier, Type componentType, ConverterComposite converterComposite) {
        super(queueSupplier, componentType, converterComposite);
    }

    @Override
    public Queue<T> convert(Object source) {
        return (Queue<T>) super.convert(source);
    }
}
