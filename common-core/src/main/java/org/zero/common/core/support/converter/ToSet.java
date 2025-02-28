package org.zero.common.core.support.converter;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/24
 */
public abstract class ToSet<T> extends ToCollection<T> {
    protected ToSet(Supplier<? extends Set<T>> setSupplier, Type componentType, ConverterComposite converterComposite) {
        super(setSupplier, componentType, converterComposite);
    }

    @Override
    public Set<T> convert(Object source) {
        return (Set<T>) super.convert(source);
    }
}
