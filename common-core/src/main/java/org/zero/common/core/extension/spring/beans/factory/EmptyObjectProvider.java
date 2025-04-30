package org.zero.common.core.extension.spring.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/13
 */
public class EmptyObjectProvider<T> implements ObjectProvider<T> {
    @Override
    public T getObject(Object... args) throws BeansException {
        return null;
    }

    @Override
    public T getIfAvailable() throws BeansException {
        return null;
    }

    @Override
    public T getIfUnique() throws BeansException {
        return null;
    }

    @Override
    public T getObject() throws BeansException {
        return null;
    }

    @Override
    public void forEach(Consumer action) {
        // do nothing
    }

    @Override
    public Stream<T> stream() {
        return Stream.empty();
    }

    @Override
    public Stream<T> orderedStream() {
        return stream();
    }
}
