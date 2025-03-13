package org.zero.common.core.util.java.reflect;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.zero.common.core.util.java.lang.ArrayUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/12
 */
@Slf4j
@UtilityClass
public class ClassLoaderUtil {
    /**
     * 默认的类加载器数组
     */
    public static final ClassLoader[] DEFAULT_CLASS_LOADERS = {
            Thread.currentThread().getContextClassLoader(),
            ClassLoaderUtil.class.getClassLoader(),
            ClassLoader.getSystemClassLoader(),
    };

    /**
     * 加载类
     * <p>
     * 使用默认类加载器：{@link ClassLoaderUtil#DEFAULT_CLASS_LOADERS}<br>
     * 默认初始化类（执行静态代码块和静态变量赋值）
     *
     * @param className 类名
     * @return 类对象，未成功时为 {@link Optional#EMPTY}
     */
    public static Optional<Class<?>> loadClassOpt(String className) {
        return loadClassOpt(className, DEFAULT_CLASS_LOADERS);
    }

    /**
     * 加载类
     * <p>
     * 默认初始化类（执行静态代码块和静态变量赋值）
     *
     * @param className    类名
     * @param classLoaders 类加载器
     * @return 类对象，未成功时为 {@link Optional#EMPTY}
     */
    public static Optional<Class<?>> loadClassOpt(String className, ClassLoader... classLoaders) {
        return loadClassOpt(className, true, classLoaders);
    }

    /**
     * 加载类
     *
     * @param className    类名
     * @param initialize   是否初始化类（执行静态代码块和静态变量赋值）
     * @param classLoaders 类加载器
     * @return 类对象，未成功时为 {@link Optional#EMPTY}
     */
    public static Optional<Class<?>> loadClassOpt(String className, boolean initialize, ClassLoader... classLoaders) {
        return Optional.of(Collections.singleton(className))
                .map(classNames -> loadClass(classNames, initialize, classLoaders))
                .map(map -> map.get(className));
    }

    /**
     * 加载类
     * <p>
     * 使用默认类加载器：{@link ClassLoaderUtil#DEFAULT_CLASS_LOADERS}<br>
     * 默认初始化类（执行静态代码块和静态变量赋值）
     *
     * @param classNames 类名
     * @return 类对象 {@link Map}，未成功时 {@code value} 为 {@code null}
     */
    public static Map<String, Class<?>> loadClass(String... classNames) {
        return loadClass(Arrays.asList(classNames), DEFAULT_CLASS_LOADERS);
    }

    /**
     * 加载类
     * <p>
     * 默认初始化类（执行静态代码块和静态变量赋值）
     *
     * @param classNames   类名
     * @param classLoaders 类加载器
     * @return 类对象 {@link Map}，未成功时 {@code value} 为 {@code null}
     */
    public static Map<String, Class<?>> loadClass(Collection<String> classNames, ClassLoader... classLoaders) {
        return loadClass(classNames, true, classLoaders);
    }

    /**
     * 加载类
     *
     * @param classNames   类名
     * @param initialize   是否初始化类（执行静态代码块和静态变量赋值）
     * @param classLoaders 类加载器
     * @return 类对象 {@link Map}，未成功时 {@code value} 为 {@code null}
     */
    public static Map<String, Class<?>> loadClass(Collection<String> classNames, boolean initialize, ClassLoader... classLoaders) {
        if (ArrayUtil.isEmpty(classLoaders)) {
            classLoaders = DEFAULT_CLASS_LOADERS;
        }
        Map<String, Class<?>> classMap = new LinkedHashMap<>(classNames.size());
        for (String className : classNames) {
            Class<?> clazz = null;
            for (ClassLoader classLoader : classLoaders) {
                try {
                    String name = className.replace('/', '.');
                    clazz = Class.forName(name, initialize, classLoader);
                    break;
                } catch (Throwable throwable) {
                    log.debug(String.format("Load class[%s] error, skipped", className), throwable);
                }
            }
            classMap.put(className, clazz);
        }
        return classMap;
    }
}
