package org.zero.common.core.util.java.reflect;

import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;
import org.zero.common.core.util.java.lang.ArrayUtil;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.ThrowableUtil;
import org.zero.common.data.constant.StringPool;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/12
 */
@Log
@UtilityClass
public class ClassLoaderUtil {
    /**
     * 默认的类加载器数组
     */
    public static final ClassLoader[] CLASS_LOADERS = Stream.of(getContext(), getDefault(), getSystem())
            .filter(Objects::nonNull)
            .toArray(ClassLoader[]::new);

    public static ClassLoader getContext() {
        return ThrowableUtil.ignore(() -> {
            if (Objects.isNull(System.getSecurityManager())) {
                return Thread.currentThread().getContextClassLoader();
            } else {
                // 绕开权限检查
                return AccessController.doPrivileged(
                        (PrivilegedAction<ClassLoader>) Thread.currentThread()::getContextClassLoader);
            }
        });
    }

    public static ClassLoader getDefault() {
        return ThrowableUtil.ignore(() -> {
            if (Objects.isNull(System.getSecurityManager())) {
                return ClassLoaderUtil.class.getClassLoader();
            } else {
                // 绕开权限检查
                return AccessController.doPrivileged(
                        (PrivilegedAction<ClassLoader>) ClassLoaderUtil.class::getClassLoader);
            }
        });
    }

    public static ClassLoader getSystem() {
        return ThrowableUtil.ignore(() -> {
            if (Objects.isNull(System.getSecurityManager())) {
                return ClassLoader.getSystemClassLoader();
            } else {
                // 绕开权限检查
                return AccessController.doPrivileged(
                        (PrivilegedAction<ClassLoader>) ClassLoader::getSystemClassLoader);
            }
        });
    }

    public static ClassLoader get() {
        return ArrayUtil.get(CLASS_LOADERS, 0);
    }

    /**
     * 加载类
     * <p>
     * 使用默认类加载器：{@link ClassLoaderUtil#CLASS_LOADERS}<br>
     * 默认初始化类（执行静态代码块和静态变量赋值）
     *
     * @param className 类名
     * @return 类对象，未成功时为 {@link Optional#EMPTY}
     */
    public static Optional<Class<?>> loadClassOpt(CharSequence className) {
        return loadClassOpt(className, CLASS_LOADERS);
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
    public static Optional<Class<?>> loadClassOpt(CharSequence className, ClassLoader... classLoaders) {
        return loadClassOpt(className, true, classLoaders);
    }

    /**
     * 加载类
     *
     * @param className  类名
     * @param initialize 是否初始化类（执行静态代码块和静态变量赋值）
     * @return 类对象，未成功时为 {@link Optional#EMPTY}
     */
    public static Optional<Class<?>> loadClassOpt(CharSequence className, boolean initialize) {
        return loadClassOpt(className, initialize, CLASS_LOADERS);
    }

    /**
     * 加载类
     *
     * @param className    类名
     * @param initialize   是否初始化类（执行静态代码块和静态变量赋值）
     * @param classLoaders 类加载器
     * @return 类对象，未成功时为 {@link Optional#EMPTY}
     */
    public static Optional<Class<?>> loadClassOpt(CharSequence className, boolean initialize, ClassLoader... classLoaders) {
        return Optional.of(Collections.singleton(className))
                .map(classNames -> loadClass(classNames, initialize, classLoaders))
                .map(map -> map.get(Objects.toString(className, null)));
    }

    /**
     * 加载类
     * <p>
     * 使用默认类加载器：{@link ClassLoaderUtil#CLASS_LOADERS}<br>
     * 默认初始化类（执行静态代码块和静态变量赋值）
     *
     * @param classNames 类名
     * @return 类对象 {@link Map}，未成功时 {@code value} 为 {@code null}
     */
    public static Map<String, Class<?>> loadClass(CharSequence... classNames) {
        return loadClass(Arrays.asList(classNames), CLASS_LOADERS);
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
    public static <S extends CharSequence> Map<String, Class<?>> loadClass(Collection<S> classNames, ClassLoader... classLoaders) {
        return loadClass(classNames, true, classLoaders);
    }

    /**
     * 加载类
     *
     * @param classNames 类名
     * @param initialize 是否初始化类（执行静态代码块和静态变量赋值）
     * @return 类对象 {@link Map}，未成功时 {@code value} 为 {@code null}
     */
    public static <S extends CharSequence> Map<String, Class<?>> loadClass(Collection<S> classNames, boolean initialize) {
        return loadClass(classNames, initialize, CLASS_LOADERS);
    }

    /**
     * 加载类
     *
     * @param classNames   类名
     * @param initialize   是否初始化类（执行静态代码块和静态变量赋值）
     * @param classLoaders 类加载器
     * @return 类对象 {@link Map}，未成功时 {@code value} 为 {@code null}
     */
    public static <S extends CharSequence> Map<String, Class<?>> loadClass(Collection<S> classNames, boolean initialize, ClassLoader... classLoaders) {
        if (ArrayUtil.isEmpty(classLoaders)) {
            classLoaders = CLASS_LOADERS;
        }
        Map<String, Class<?>> classMap = new LinkedHashMap<>(classNames.size());
        for (CharSequence className : classNames) {
            Class<?> clazz = null;
            if (CharSequenceUtil.nonBlank(className)) {
                String name = className.toString();
                if (name.contains(StringPool.SLASH)) {
                    name = name.replace(StringPool.SLASH, StringPool.DOT);
                }
                for (ClassLoader classLoader : classLoaders) {
                    try {
                        clazz = Class.forName(name, initialize, classLoader);
                    } catch (Throwable throwable) {
                        log.log(Level.WARNING, String.format("Load class[%s] error, skipped", className), throwable);
                    }
                }
            }
            classMap.put(Objects.toString(className, null), clazz);
        }
        return classMap;
    }
}
