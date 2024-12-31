package org.zero.common.core.util.java.lang;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

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

    /**
     * 获取指定包下类对象列表
     */
    public static List<Class<?>> getClasses(String packageName) {
        List<Class<?>> classes = new LinkedList<>();
        List<String> classNames = getClassNames(packageName);
        classNames.forEach(className -> {
            try {
                classes.add(Class.forName(className));
            } catch (Throwable throwable) {
                log.warn(String.format("Get class[%s] exception under package[%s], skipped", className, packageName), throwable);
            }
        });
        return classes;
    }

    /**
     * 获取指定包下类名列表
     */
    @SneakyThrows
    public static List<String> getClassNames(String packageName) {
        List<String> classNames = new LinkedList<>();
        String packageFileName = packageName.replace(PACKAGE_SEPARATOR, CLASS_FILE_SEPARATOR);
        Enumeration<URL> urls = Thread.currentThread()
                .getContextClassLoader()
                .getResources(packageFileName);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                String packagePath = url.getPath()
                        .replace("%5c", CLASS_FILE_SEPARATOR)
                        .replace("%20", " ");
                File file = new File(packagePath);
                List<String> classNamesByFile = getClassNamesByFile(file, packageName);
                classNames.addAll(classNamesByFile);
            } else if ("jar".equals(protocol)) {
                JarFile jarFile = ((JarURLConnection) url.openConnection())
                        .getJarFile();
                if (Objects.nonNull(jarFile)) {
                    List<String> classNamesByJar = getClassNamesByJar(jarFile, packageName);
                    classNames.addAll(classNamesByJar);
                }
            }
        }
        return classNames;
    }

    /**
     * 在文件中获取指定包名的类名
     */
    public static List<String> getClassNamesByFile(File file, String packageName) {
        return getClassNamesByFile(file, packageName, true, false);
    }

    /**
     * 在文件中获取指定包名的类名
     */
    public static List<String> getClassNamesByFile(File file, String packageName, boolean withChildClass, boolean withInnerClass) {
        List<String> classNames = new LinkedList<>();
        // File 对象为 null 或其不存在
        if (Objects.isNull(file) || !file.exists()) {
            return classNames;
        }
        // File 对象是目录且又不包含子包类
        if (!withChildClass && file.isDirectory()) {
            return classNames;
        }
        if (file.isFile()) {
            String path = file.getPath();
            // 不包含内部类但存在内部类标识
            if (!withInnerClass && path.contains(INNER_CLASS_SEPARATOR)) {
                return classNames;
            }
            // 存在类文件标识（）
            if (path.endsWith(CLASS_FILE_SUFFIX) && path.contains(CLASS_FILE_PREFIX)) {
                String classFileName = path.substring(path.indexOf(CLASS_FILE_PREFIX) + CLASS_FILE_PREFIX.length())
                        .replace(CLASS_FILE_SEPARATOR, PACKAGE_SEPARATOR);
                String className = classFileName.substring(0, classFileName.lastIndexOf(PACKAGE_SEPARATOR));
                if (withChildClass) {
                    if (className.startsWith(packageName)) {
                        classNames.add(className);
                    }
                } else {
                    if (packageName.equals(className.substring(0, className.lastIndexOf(PACKAGE_SEPARATOR)))) {
                        classNames.add(className);
                    }
                }
            }
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (Objects.nonNull(files)) {
                for (File f : files) {
                    List<String> classNamesByFile = getClassNamesByFile(f, packageName, withChildClass, withInnerClass);
                    classNames.addAll(classNamesByFile);
                }
            }
        }
        return classNames;
    }

    /**
     * 通过JarFile获取其中全限定类名
     */
    public static List<String> getClassNamesByJar(JarFile jarFile, String packageName) {
        return getClassNamesByJar(jarFile, packageName, true, false);
    }

    /**
     * 通过JarFile获取其中全限定类名
     */
    public static List<String> getClassNamesByJar(JarFile jarFile, String packageName, boolean withChildClass, boolean withInnerClass) {
        List<String> classNames = new LinkedList<>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String jarEntryName = jarEntry.getName();
            // 是 class 文件
            if (jarEntryName.endsWith(CLASS_FILE_SUFFIX)) {
                String replacedJarEntryName = jarEntryName.replace(CLASS_FILE_SUFFIX, "")
                        .replace(CLASS_FILE_SEPARATOR, PACKAGE_SEPARATOR);
                if (!withInnerClass && replacedJarEntryName.contains(INNER_CLASS_SEPARATOR)) {
                    continue;
                }
                if (withChildClass) {
                    if (replacedJarEntryName.startsWith(packageName)) {
                        classNames.add(replacedJarEntryName);
                    }
                } else {
                    if (packageName.equals(replacedJarEntryName.substring(0, replacedJarEntryName.lastIndexOf(PACKAGE_SEPARATOR)))) {
                        classNames.add(replacedJarEntryName);
                    }
                }
            }
        }
        return classNames;
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
}
