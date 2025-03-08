package org.zero.common.core.util.java.reflect;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.zero.common.core.exception.AnyThrow;
import org.zero.common.core.extension.java.TypeReference;
import org.zero.common.core.util.java.lang.PrimitiveType;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.JarURLConnection;
import java.net.URL;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zero
 * @since 2023/7/17
 */
@Slf4j
@UtilityClass
public class ClassUtil {
    private static final String CLASS_FILE_SUFFIX = ".class";
    private static final String CLASS_FILE_PREFIX = File.separator + "classes" + File.separator;
    private static final String CLASS_FILE_SEPARATOR = File.separator;
    private static final String PACKAGE_SEPARATOR = ".";
    private static final String INNER_CLASS_SEPARATOR = "$";

    public static Collection<Class<?>> getClasses(Package p) {
        return getClasses(p.getName());
    }

    public static Collection<Class<?>> getClasses(String packageName) {
        return getClassMap(packageName).values();
    }

    public static Map<String, Class<?>> getClassMap(Package p) {
        return getClassMap(p.getName());
    }

    public static Map<String, Class<?>> getClassMap(String packageName) {
        Map<String, Class<?>> classMap = new LinkedHashMap<>();
        Collection<String> classNames = getClassNames(packageName);
        for (String className : classNames) {
            Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (Throwable throwable) {
                log.debug(String.format("Get class[%s] exception under package[%s], skipped", className, packageName), throwable);
                clazz = null;
            }
            classMap.put(className, clazz);
        }
        return classMap;
    }

    public static Collection<String> getClassNames(Package p) {
        return getClassNames(p.getName());
    }

    public static Collection<String> getClassNames(String packageName) {
        String packageFileName = packageName.replace(PACKAGE_SEPARATOR, CLASS_FILE_SEPARATOR);
        Enumeration<URL> urls = AnyThrow.sneakyThrow(Thread.currentThread().getContextClassLoader(),
                classLoader -> classLoader.getResources(packageFileName));
        return Collections.list(urls)
                .stream()
                .flatMap(url -> getClassNamesByUrl(url, packageName).stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Collection<String> getClassNamesByUrl(URL url, String packageName) {
        return getClassNamesByUrl(url, packageName, true, false);
    }

    public static Collection<String> getClassNamesByUrl(URL url, String packageName, boolean withChildClass, boolean withInnerClass) {
        String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
            return Optional.ofNullable(AnyThrow.sneakyThrow(url, URL::toURI))
                    .map(File::new)
                    .map(file -> getClassNamesByFile(file, packageName, withChildClass, withInnerClass))
                    .orElseGet(LinkedHashSet::new);
        }
        if ("jar".equals(protocol)) {
            return Optional.ofNullable(AnyThrow.sneakyThrow(url, URL::openConnection))
                    .filter(JarURLConnection.class::isInstance)
                    .map(JarURLConnection.class::cast)
                    .map(AnyThrow.sneakyThrow(JarURLConnection::getJarFile))
                    .map(jarFile -> getClassNamesByJar(jarFile, packageName, withChildClass, withInnerClass))
                    .orElseGet(LinkedHashSet::new);
        }
        return Collections.emptyList();
    }

    public static Collection<String> getClassNamesByFile(File file, String packageName) {
        return getClassNamesByFile(file, packageName, true, false);
    }

    public static Collection<String> getClassNamesByFile(File file, String packageName, boolean withChildClass, boolean withInnerClass) {
        return Optional.ofNullable(file)
                .filter(File::exists)
                .filter(f -> withChildClass || !f.isDirectory())
                .<Collection<String>>map(f -> {
                    if (f.isFile()) {
                        return Optional.of(f.getPath())
                                .filter(filePath -> withInnerClass || !filePath.contains(INNER_CLASS_SEPARATOR))
                                .filter(filePath -> filePath.endsWith(CLASS_FILE_SUFFIX) && filePath.contains(CLASS_FILE_PREFIX))
                                .map(filePath -> filePath.substring(filePath.indexOf(CLASS_FILE_PREFIX) + CLASS_FILE_PREFIX.length())
                                        .replace(CLASS_FILE_SEPARATOR, PACKAGE_SEPARATOR)
                                        .replace(CLASS_FILE_SUFFIX, ""))
                                .filter(className -> {
                                    if (withChildClass) {
                                        return className.startsWith(packageName);
                                    }
                                    return packageName.equals(className.substring(0, className.lastIndexOf(PACKAGE_SEPARATOR)));
                                })
                                .<Collection<String>>map(Collections::singletonList)
                                .orElseGet(LinkedHashSet::new);
                    }
                    if (f.isDirectory()) {
                        return Optional.ofNullable(f.listFiles())
                                .map(Stream::of)
                                .orElseGet(Stream::empty)
                                .flatMap(nextFile -> getClassNamesByFile(nextFile, packageName, withChildClass, withInnerClass).stream())
                                .collect(Collectors.toCollection(LinkedHashSet::new));
                    }
                    return Collections.emptyList();
                })
                .orElseGet(LinkedHashSet::new);
    }

    public static Collection<String> getClassNamesByJar(JarFile jarFile, String packageName) {
        return getClassNamesByJar(jarFile, packageName, true, false);
    }

    public static Collection<String> getClassNamesByJar(JarFile jarFile, String packageName, boolean withChildClass, boolean withInnerClass) {
        return Optional.ofNullable(jarFile)
                .map(JarFile::stream)
                .orElseGet(Stream::empty)
                .map(JarEntry::getName)
                .filter(entryName -> entryName.endsWith(CLASS_FILE_SUFFIX))
                .map(entryName -> entryName.replace(CLASS_FILE_SUFFIX, "").replace(CLASS_FILE_SEPARATOR, PACKAGE_SEPARATOR))
                .filter(className -> withInnerClass || !className.contains(INNER_CLASS_SEPARATOR))
                .filter(className -> {
                    if (withChildClass) {
                        return className.startsWith(packageName);
                    }
                    return packageName.equals(className.substring(0, className.lastIndexOf(PACKAGE_SEPARATOR)));
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * 是否是指定包下的类
     */
    public static boolean isSpecifiedClass(Class<?> clazz, String regex) {
        return Optional.ofNullable(clazz)
                .map(Class::getPackage)
                .map(Package::getName)
                .map(name -> Pattern.matches(regex, name))
                .orElse(Boolean.FALSE);
    }

    /**
     * 是否是指定包下的类
     */
    public static boolean isSpecifiedClassWithPrefix(Class<?> clazz, String... packageNames) {
        return Optional.ofNullable(clazz)
                .map(Class::getPackage)
                .map(Package::getName)
                .map(name -> {
                    for (String packageName : packageNames) {
                        if (name.startsWith(packageName)) {
                            return true;
                        }
                    }
                    return false;
                })
                .orElse(Boolean.FALSE);
    }

    /**
     * 是否是指定包下的类
     */
    public static boolean isSpecifiedClass(Class<?> clazz, String... packageNames) {
        return Optional.ofNullable(clazz)
                .map(Class::getPackage)
                .map(Package::getName)
                .map(name -> {
                    for (String packageName : packageNames) {
                        if (Objects.equals(name, packageName)) {
                            return true;
                        }
                    }
                    return false;
                })
                .orElse(Boolean.FALSE);
    }

    /**
     * 是否是数字类型
     */
    public static boolean isNumClass(Class<?> clazz) {
        return Objects.nonNull(clazz) &&
                (Number.class.isAssignableFrom(clazz) ||
                        (clazz.isPrimitive() &&
                                (clazz == int.class || clazz == long.class ||
                                        clazz == short.class || clazz == byte.class ||
                                        clazz == float.class || clazz == double.class)));
    }

    /**
     * 是否是日期时间类型
     */
    public static boolean isDateTimeClass(Class<?> clazz) {
        return Objects.nonNull(clazz) &&
                (Date.class.isAssignableFrom(clazz) ||
                        Calendar.class.isAssignableFrom(clazz) ||
                        TemporalAccessor.class.isAssignableFrom(clazz));
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj, Type type) {
        if (Objects.isNull(obj)) {
            return null;
        }
        if (type instanceof Class) {
            Class<T> clazz = (Class<T>) type;
            return clazz.cast(obj);
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            return cast(obj, rawType);
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            Type componentType = genericArrayType.getGenericComponentType();
            Object[] inputArray = (Object[]) obj;
            int length = inputArray.length;
            Class<?> componentClass = getRawClass(componentType);
            Object outputArray = Array.newInstance(componentClass, length);
            for (int i = 0; i < length; i++) {
                Array.set(outputArray, i, cast(inputArray[i], componentType));
            }
            return (T) outputArray;
        }
        if (type instanceof TypeVariable) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) type;
            Type[] bounds = typeVariable.getBounds();
            Type boundType = bounds.length > 0 ? bounds[0] : Object.class;
            return cast(obj, boundType);
        }
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds.length > 0 && upperBounds[0] != Object.class) {
                return cast(obj, upperBounds[0]);
            }
            Type[] lowerBounds = wildcardType.getLowerBounds();
            if (lowerBounds.length > 0) {
                return cast(obj, lowerBounds[0]);
            }
        }
        if (type instanceof TypeReference) {
            TypeReference<T> typeReference = (TypeReference<T>) type;
            Type referenceType = typeReference.getType();
            return cast(obj, referenceType);
        }
        return (T) obj;
    }

    public static Class<?> getRawClass(Type type) {
        if (type == null) {
            return null;
        }
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            return getRawClass(((ParameterizedType) type).getRawType());
        }
        if (type instanceof GenericArrayType) {
            Class<?> componentClass = getRawClass(((GenericArrayType) type).getGenericComponentType());
            return Array.newInstance(componentClass, 0).getClass();
        }
        if (type instanceof TypeVariable) {
            Type[] bounds = ((TypeVariable<?>) type).getBounds();
            return bounds.length > 0 ? getRawClass(bounds[0]) : Object.class;
        }
        return Object.class;
    }

    /**
     * 检查目标类是否可以从原类转化<br>
     * 转化包括：<br>
     * 1、原类是对象，目标类型是原类型实现的接口<br>
     * 2、目标类型是原类型的父类<br>
     * 3、两者是原始类型或者包装类型（相互转换）
     *
     * @param targetClass 目标类型
     * @param sourceClass 原类型
     * @return 是否可转化
     */
    public static boolean isAssignable(Class<?> targetClass, Class<?> sourceClass) {
        if (Objects.isNull(targetClass) || Objects.isNull(sourceClass)) {
            return false;
        }
        // 对象类型
        if (targetClass.isAssignableFrom(sourceClass)) {
            return true;
        }
        // 基本类型
        if (targetClass.isPrimitive()) {
            Class<?> resolvedPrimitive = PrimitiveType.unwrap(sourceClass);
            return targetClass.equals(resolvedPrimitive);
        }
        // 包装类型
        Class<?> resolvedWrapper = PrimitiveType.wrap(sourceClass);
        return Objects.nonNull(resolvedWrapper) && targetClass.isAssignableFrom(resolvedWrapper);
    }

    /**
     * 检查目标类型是否可以从源类型转化
     * <p>
     * 注意：该方法支持 Java 中可强转的和可以相互兼容的类型，因此当该方法返回 true，不一定可强转。
     *
     * @param targetType 目标类型，如：Number.class
     * @param sourceType 源类型，如：Integer.class
     * @return 是否可以转化
     */
    public static boolean isConvertible(Type targetType, Type sourceType) {
        if (Objects.isNull(targetType) || Objects.isNull(sourceType)) {
            return false;
        }
        if (targetType.equals(sourceType)) {
            return true;
        }
        if (targetType instanceof Class) {
            Class<?> targetClass = (Class<?>) targetType;
            if (sourceType instanceof Class) {
                Class<?> sourceClass = (Class<?>) sourceType;
                // 数组类型
                if (targetClass.isArray() && sourceClass.isArray()) {
                    return isConvertible(targetClass.getComponentType(), sourceClass.getComponentType());
                }
                return isAssignable(targetClass, sourceClass);
            }
            if (sourceType instanceof GenericArrayType && targetClass.isArray()) {
                GenericArrayType sourceGenericArrayType = (GenericArrayType) sourceType;
                return isConvertible(targetClass.getComponentType(), sourceGenericArrayType.getGenericComponentType());
            }
            if (sourceType instanceof ParameterizedType) {
                ParameterizedType sourceParameterizedType = (ParameterizedType) sourceType;
                return isConvertible(targetClass, sourceParameterizedType.getRawType());
            }
            return false;
        }
        if (targetType instanceof ParameterizedType) {
            ParameterizedType targetParameterizedType = (ParameterizedType) targetType;
            if (sourceType instanceof ParameterizedType) {
                ParameterizedType sourceParameterizedType = (ParameterizedType) sourceType;
                Type targetRawType = targetParameterizedType.getRawType();
                Type sourceRawType = sourceParameterizedType.getRawType();
                if (!isConvertible(targetRawType, sourceRawType)) {
                    return false;
                }
                Type[] targetActualTypeArguments = targetParameterizedType.getActualTypeArguments();
                Type[] sourceActualTypeArguments = sourceParameterizedType.getActualTypeArguments();
                if (targetActualTypeArguments.length != sourceActualTypeArguments.length) {
                    return false;
                }
                for (int i = 0; i < targetActualTypeArguments.length && i < sourceActualTypeArguments.length; i++) {
                    Type targetActualTypeArgument = targetActualTypeArguments[i];
                    Type sourceActualTypeArgument = sourceActualTypeArguments[i];
                    if (!isConvertible(targetActualTypeArgument, sourceActualTypeArgument)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        if (targetType instanceof GenericArrayType) {
            GenericArrayType targetGenericArrayType = (GenericArrayType) targetType;
            if (sourceType instanceof Class) {
                Class<?> sourceClass = (Class<?>) sourceType;
                if (sourceClass.isArray()) {
                    return isConvertible(targetGenericArrayType.getGenericComponentType(), sourceClass.getComponentType());
                }
                return false;
            }
            if (sourceType instanceof GenericArrayType) {
                GenericArrayType sourceGenericArrayType = (GenericArrayType) sourceType;
                return isConvertible(targetGenericArrayType.getGenericComponentType(), sourceGenericArrayType.getGenericComponentType());
            }
            return false;
        }
        return false;
    }

    /**
     * 获取指定类型的默认值
     */
    public static Object getDefaultValue(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return null;
        }
        // 原始类型
        if (clazz.isPrimitive()) {
            if (long.class == clazz) {
                return 0L;
            } else if (int.class == clazz) {
                return 0;
            } else if (short.class == clazz) {
                return (short) 0;
            } else if (byte.class == clazz) {
                return (byte) 0;
            } else if (char.class == clazz) {
                return '\u0000';
            } else if (double.class == clazz) {
                return 0.0;
            } else if (float.class == clazz) {
                return 0.0F;
            } else if (boolean.class == clazz) {
                return false;
            }
        }
        // 引用类型
        return null;
    }

    public static Object[] getArray(Object source) {
        if (Objects.isNull(source)) {
            return null;
        }
        Class<?> clazz = source.getClass();
        if (clazz.isArray()) {
            Object[] array;
            Class<?> componentType = clazz.getComponentType();
            if (componentType.isPrimitive()) {
                // 处理原始类型数组（如int[]）
                int length = Array.getLength(source);
                array = new Object[length];
                for (int i = 0; i < length; i++) {
                    array[i] = Array.get(source, i);
                }
            } else {
                // 处理对象数组（如String[]）
                array = (Object[]) source;
            }
            return array;
        }
        return null;
    }
}
