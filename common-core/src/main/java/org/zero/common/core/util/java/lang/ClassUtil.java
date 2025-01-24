package org.zero.common.core.util.java.lang;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.zero.common.core.exception.AnyThrow;

import java.io.File;
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

    /**
     * 获取指定类型的默认值
     */
    public static Object getDefaultValue(Class<?> clazz) {
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
        return null;
    }
}
