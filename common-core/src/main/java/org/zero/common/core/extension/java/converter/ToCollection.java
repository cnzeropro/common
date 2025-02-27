package org.zero.common.core.extension.java.converter;

import org.zero.common.core.extension.java.collection.EnumerationIterable;
import org.zero.common.core.extension.java.collection.IteratorIterable;
import org.zero.common.core.util.java.lang.ClassUtil;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/24
 */
public abstract class ToCollection<T> implements GenericConverter<Collection<T>> {
    protected final Supplier<? extends Collection<T>> collectionSupplier;
    protected final Type componentType;
    protected final ConverterComposite converterComposite;

    protected ToCollection(Supplier<? extends Collection<T>> collectionSupplier, Type componentType, ConverterComposite converterComposite) {
        this.collectionSupplier = collectionSupplier;
        this.componentType = componentType;
        this.converterComposite = converterComposite;
    }

    @Override
    public Collection<T> convert(Object source) {
        if (Objects.isNull(source)) {
            return null;
        }
        Class<?> sourceClass = source.getClass();
        if (sourceClass.isArray()) {
            Object[] array = ClassUtil.getArray(source);
            Iterable<?> iterable = Arrays.asList(array);
            return this.convertFromIterable(iterable);
        }
        if (source instanceof Iterator) {
            Iterator<?> iterator = (Iterator<?>) source;
            Iterable<?> iterable = IteratorIterable.of(iterator);
            return this.convertFromIterable(iterable);
        }
        if (source instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) source;
            return this.convertFromIterable(iterable);
        }
        if (source instanceof Enumeration) {
            Enumeration<?> enumeration = (Enumeration<?>) source;
            Iterable<?> iterable = EnumerationIterable.of(enumeration);
            return this.convertFromIterable(iterable);
        }
        Iterable<?> iterable = Collections.singletonList(source);
        return this.convertFromIterable(iterable);
    }

    protected Collection<T> convertFromIterable(Iterable<?> iterable) {
        Collection<T> result = collectionSupplier.get();
        for (Object o : iterable) {
            T t = converterComposite.convertExactAndQuietly(componentType, o);
            result.add(t);
        }
        return result;
    }
}
