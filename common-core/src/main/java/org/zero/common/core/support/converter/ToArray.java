package org.zero.common.core.support.converter;

import org.zero.common.core.extension.java.collection.EnumerationIterator;
import org.zero.common.core.util.java.lang.ClassUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/17
 */
public abstract class ToArray<T> implements GenericConverter<T[]> {
    protected final Class<T> componentType;
    protected final ConverterComposite converterComposite;

    protected ToArray(Class<T> componentType, ConverterComposite converterComposite) {
        this.converterComposite = converterComposite;
        this.componentType = componentType;
    }

    /**
     * 子类需要重写以明确转换类型（方法返回类型）
     * <p>
     * 注意：如不重写，最好显式使用类型注册到 {@link ConverterComposite}
     */
    @Override
    public T[] convert(Object source) {
        if (Objects.isNull(source)) {
            return null;
        }
        Class<?> sourceClass = source.getClass();
        if (sourceClass.isArray()) {
            Object[] array = ClassUtil.getArray(source);
            return this.convertArrayElement(array);
        }
        if (source instanceof Collection) {
            Collection<?> collection = (Collection<?>) source;
            Object[] array = collection.toArray();
            return this.convertArrayElement(array);
        }
        if (source instanceof Iterator) {
            Iterator<?> iterator = (Iterator<?>) source;
            Object[] array = this.getElementArray(iterator);
            return this.convertArrayElement(array);
        }
        if (source instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) source;
            Iterator<?> iterator = iterable.iterator();
            Object[] array = this.getElementArray(iterator);
            return this.convertArrayElement(array);
        }
        if (source instanceof Enumeration) {
            Enumeration<?> enumeration = (Enumeration<?>) source;
            Iterator<?> iterator = EnumerationIterator.of(enumeration);
            Object[] array = this.getElementArray(iterator);
            return this.convertArrayElement(array);
        }
        Object[] array = new Object[]{source};
        return this.convertArrayElement(array);
    }

    @SuppressWarnings("unchecked")
    private T[] convertArrayElement(Object[] array) {
        if (componentType.isAssignableFrom(array.getClass().getComponentType())) {
            return (T[]) array;
        }
        Object arrayObj = Array.newInstance(componentType, array.length);
        for (int i = 0; i < array.length; i++) {
            Object obj = array[i];
            Object convertedObj = converterComposite.convertQuietly(componentType, obj,false);
            Array.set(arrayObj, i, convertedObj);
        }
        return (T[]) arrayObj;
    }

    private Object[] getElementArray(Iterator<?> iterator) {
        List<Object> result = new ArrayList<>();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            result.add(next);
        }
        return result.toArray();
    }

    // private Object[] getElementArray(Iterator<?> iterator) {
    //     int i = 0;
    //     Object arrayObj = Array.newInstance(Object.class, i);
    //     while (iterator.hasNext()) {
    //         arrayObj = Array.newInstance(Object.class, i + 1);
    //         Object next = iterator.next();
    //         Array.set(arrayObj, i, next);
    //         i++;
    //     }
    //     return (Object[]) arrayObj;
    // }
}
