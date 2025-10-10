package org.zero.common.core.util.java.util;

import org.zero.common.core.util.java.lang.ClassLoaderUtil;
import org.zero.common.core.util.java.lang.ObjectUtil;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/12
 */
public class ServiceLoaderUtil {
    public static <T> T loadFirstAvailable(Class<T> clazz) {
        return loadFirstAvailable(clazz, null);
    }

    public static <T> T loadFirstAvailable(Class<T> clazz, ClassLoader loader) {
        final Iterator<T> iterator = load(clazz, loader).iterator();
        while (iterator.hasNext()) {
            try {
                return iterator.next();
            } catch (ServiceConfigurationError ignored) {
            }
        }
        return null;
    }

    public static <T> T loadFirst(Class<T> clazz) {
        return loadFirst(clazz, null);
    }

    public static <T> T loadFirst(Class<T> clazz, ClassLoader loader) {
        final Iterator<T> iterator = load(clazz, loader).iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    public static <T> List<T> loadAll(Class<T> clazz) {
        return loadAll(clazz, null);
    }

    public static <T> List<T> loadAll(Class<T> clazz, ClassLoader loader) {
        return ListUtil.of(load(clazz, loader));
    }

    public static <T> ServiceLoader<T> load(Class<T> clazz) {
        return load(clazz, null);
    }

    public static <T> ServiceLoader<T> load(Class<T> clazz, ClassLoader loader) {
        return ServiceLoader.load(clazz, ObjectUtil.defaultIfNull(loader, ClassLoaderUtil.get()));
    }
}
