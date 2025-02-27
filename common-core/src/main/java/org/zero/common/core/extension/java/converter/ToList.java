package org.zero.common.core.extension.java.converter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/24
 */
public abstract class ToList<T> extends ToCollection<T> {
    protected ToList(Supplier<? extends List<T>> listSupplier, Type componentType, ConverterComposite converterComposite) {
        super(listSupplier, componentType, converterComposite);
    }

    @Override
    public List<T> convert(Object source) {
        return (List<T>) super.convert(source);
    }
}
