package org.zero.common.core.util.java;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Array;
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
            } catch (Exception e) {
                log.warn(String.format("Get class[%s] exception under package[%s], skipped", className, packageName), e);
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

    /**
     * 通过类型获取值
     *
     * @param type         类型
     * @param defaultValue 默认值
     * @return 值
     */
    public static Object getValue(Class<?> type, Object defaultValue) {
        if (Objects.isNull(type)) {
            return null;
        }

        // Primitive
        if (type.isPrimitive()) {
            return getPrimitiveValue(type, defaultValue);
        }

        if (Objects.isNull(defaultValue)) {
            return null;
        }

        if (Objects.equals(type, defaultValue.getClass())) {
            return defaultValue;
        }

        // Primitive Wrapper
        if (Long.class.isAssignableFrom(type)) {
            if (defaultValue instanceof Number) {
                return ((Number) defaultValue).longValue();
            }
            try {
                return Long.valueOf(defaultValue.toString());
            } catch (NumberFormatException ignored) {
            }
        }
        if (Integer.class.isAssignableFrom(type)) {
            if (defaultValue instanceof Number) {
                return ((Number) defaultValue).intValue();
            }
            try {
                return Integer.valueOf(defaultValue.toString());
            } catch (NumberFormatException ignored) {
            }
        }
        if (Short.class.isAssignableFrom(type)) {
            if (defaultValue instanceof Number) {
                return ((Number) defaultValue).shortValue();
            }
            try {
                return Short.valueOf(defaultValue.toString());
            } catch (NumberFormatException ignored) {
            }
        }
        if (Byte.class.isAssignableFrom(type)) {
            if (defaultValue instanceof Number) {
                return ((Number) defaultValue).byteValue();
            }
            try {
                return Byte.valueOf(defaultValue.toString());
            } catch (NumberFormatException ignored) {
            }
        }
        if (Double.class.isAssignableFrom(type)) {
            if (defaultValue instanceof Number) {
                return ((Number) defaultValue).doubleValue();
            }
            try {
                return Double.valueOf(defaultValue.toString());
            } catch (NumberFormatException ignored) {
            }
        }
        if (Float.class.isAssignableFrom(type)) {
            if (defaultValue instanceof Number) {
                return ((Number) defaultValue).floatValue();
            }
            try {
                return Float.valueOf(defaultValue.toString());
            } catch (NumberFormatException ignored) {
            }
        }
        if (Character.class.isAssignableFrom(type)) {
            String defaultValueStr = defaultValue.toString();
            if (!defaultValueStr.isEmpty()) {
                return defaultValueStr.charAt(0);
            }
        }
        if (Boolean.class.isAssignableFrom(type)) {
            return Boolean.valueOf(defaultValue.toString());
        }

        // CharSequence
        if (CharSequence.class.isAssignableFrom(type)) {
            if (Objects.equals(CharSequence.class, type)) {
                if (defaultValue instanceof CharSequence) {
                    return defaultValue;
                }
            }
            if (String.class.isAssignableFrom(type)) {
                return defaultValue.toString();
            }
        }

        // Big Number
        if (java.math.BigDecimal.class.isAssignableFrom(type)) {
            if (Objects.equals(java.math.BigDecimal.class, type)) {
                if (defaultValue instanceof java.math.BigDecimal) {
                    return defaultValue;
                }
            }
            try {
                return new java.math.BigDecimal(defaultValue.toString());
            } catch (Exception ignored) {
            }
        }
        if (java.math.BigInteger.class.isAssignableFrom(type)) {
            if (Objects.equals(java.math.BigInteger.class, type)) {
                if (defaultValue instanceof java.math.BigInteger) {
                    return defaultValue;
                }
            }
            try {
                return new java.math.BigInteger(defaultValue.toString());
            } catch (Exception ignored) {
            }
        }

        // Date
        if (java.util.Date.class.isAssignableFrom(type)) {
            if (Objects.equals(java.util.Date.class, type)) {
                if (defaultValue instanceof java.util.Date) {
                    return defaultValue;
                }
                try {
                    return DateUtil.parse(defaultValue.toString()).toJdkDate();
                } catch (Exception ignored) {
                }
            }
            if (java.sql.Date.class.isAssignableFrom(type)) {
                try {
                    return DateUtil.parse(defaultValue.toString()).toSqlDate();
                } catch (Exception ignored) {
                }
            }
            if (java.sql.Timestamp.class.isAssignableFrom(type)) {
                try {
                    return DateUtil.parse(defaultValue.toString()).toTimestamp();
                } catch (Exception ignored) {
                }
            }
            if (java.sql.Time.class.isAssignableFrom(type)) {
                try {
                    return new java.sql.Time(DateUtil.parse(defaultValue.toString()).getTime());
                } catch (Exception ignored) {
                }
            }
        }

        // Calendar
        if (java.util.Calendar.class.isAssignableFrom(type)) {
            if (Objects.equals(java.util.Calendar.class, type)) {
                if (defaultValue instanceof java.util.Calendar) {
                    return defaultValue;
                }
            }
            try {
                return DateUtil.parse(defaultValue.toString()).toCalendar();
            } catch (Exception ignored) {
            }
        }

        // TemporalAccessor
        if (java.time.temporal.TemporalAccessor.class.isAssignableFrom(type)) {
            if (Objects.equals(java.time.temporal.TemporalAccessor.class, type)) {
                if (defaultValue instanceof java.time.temporal.TemporalAccessor) {
                    return defaultValue;
                }
            }
            if (java.time.LocalDateTime.class.isAssignableFrom(type)) {
                try {
                    return DateUtil.parse(defaultValue.toString()).toLocalDateTime();
                } catch (Exception ignored) {
                }
            }
            if (java.time.LocalDate.class.isAssignableFrom(type)) {
                try {
                    return DateUtil.parse(defaultValue.toString()).toLocalDateTime().toLocalDate();
                } catch (Exception ignored) {
                }
            }
            if (java.time.LocalTime.class.isAssignableFrom(type)) {
                try {
                    return DateUtil.parse(defaultValue.toString()).toLocalDateTime().toLocalTime();
                } catch (Exception ignored) {
                }
            }
            if (java.time.Instant.class.isAssignableFrom(type)) {
                try {
                    return DateUtil.parse(defaultValue.toString()).toInstant();
                } catch (Exception ignored) {
                }
            }
            if (java.time.ZonedDateTime.class.isAssignableFrom(type)) {
                try {
                    DateTime dateTime = DateUtil.parse(defaultValue.toString());
                    return java.time.ZonedDateTime.ofInstant(dateTime.toInstant(), dateTime.getZoneId());
                } catch (Exception ignored) {
                }
            }
            if (java.time.OffsetDateTime.class.isAssignableFrom(type)) {
                try {
                    DateTime dateTime = DateUtil.parse(defaultValue.toString());
                    return java.time.OffsetDateTime.ofInstant(dateTime.toInstant(), dateTime.getZoneId());
                } catch (Exception ignored) {
                }
            }
            if (java.time.OffsetTime.class.isAssignableFrom(type)) {
                try {
                    DateTime dateTime = DateUtil.parse(defaultValue.toString());
                    return java.time.OffsetTime.ofInstant(dateTime.toInstant(), dateTime.getZoneId());
                } catch (Exception ignored) {
                }
            }
            if (java.time.Year.class.isAssignableFrom(type)) {
                try {
                    return java.time.Year.of(DateUtil.parse(defaultValue.toString()).toLocalDateTime().getYear());
                } catch (Exception ignored) {
                }
            }
            if (java.time.YearMonth.class.isAssignableFrom(type)) {
                try {
                    java.time.LocalDateTime dateTime = DateUtil.parse(defaultValue.toString()).toLocalDateTime();
                    return java.time.YearMonth.of(dateTime.getYear(), dateTime.getMonth());
                } catch (Exception ignored) {
                }
            }
            if (java.time.MonthDay.class.isAssignableFrom(type)) {
                try {
                    java.time.LocalDateTime dateTime = DateUtil.parse(defaultValue.toString()).toLocalDateTime();
                    return java.time.MonthDay.of(dateTime.getMonth(), dateTime.getDayOfMonth());
                } catch (Exception ignored) {
                }
            }
        }

        // Enum
        if (type.isEnum()) {
            Enum<?>[] enumConstants = (Enum<?>[]) type.getEnumConstants();
            if (defaultValue instanceof Number) {
                int i = ((Number) defaultValue).intValue();
                if (i >= 0 && i < enumConstants.length) {
                    return enumConstants[i];
                }
            }
            if (defaultValue instanceof CharSequence) {
                for (Enum<?> enumConstant : enumConstants) {
                    if (enumConstant.name().equalsIgnoreCase(defaultValue.toString())) {
                        return enumConstant;
                    }
                }
            }
        }

        // Array
        if (type.isArray()) {
            Class<?> componentType = type.getComponentType();
            Class<?> defaultValueClass = defaultValue.getClass();
            if (Objects.equals(componentType, defaultValueClass)) {
                try {
                    Object array = Array.newInstance(defaultValueClass, 1);
                    Array.set(array, 0, defaultValue);
                    return array;
                } catch (Exception ignored) {
                }
            }
            try {
                return Array.newInstance(componentType, 0);
            } catch (Exception ignored) {
            }
        }

        // Collection
        if (java.util.Collection.class.isAssignableFrom(type)) {
            if (java.util.List.class.isAssignableFrom(type)) {
                if (Objects.equals(java.util.List.class, type)) {
                    if (defaultValue instanceof java.util.List) {
                        return defaultValue;
                    }
                    return new java.util.ArrayList<>();
                }
            }
            if (java.util.Set.class.isAssignableFrom(type)) {
                if (java.util.SortedSet.class.isAssignableFrom(type)) {
                    if (java.util.NavigableSet.class.isAssignableFrom(type)) {
                        if (Objects.equals(java.util.NavigableSet.class, type)) {
                            if (defaultValue instanceof java.util.NavigableSet) {
                                return defaultValue;
                            }
                            return new java.util.TreeSet<>();
                        }
                    }
                    if (Objects.equals(java.util.SortedSet.class, type)) {
                        if (defaultValue instanceof java.util.SortedSet) {
                            return defaultValue;
                        }
                        return new java.util.TreeSet<>();
                    }
                }
                if (Objects.equals(java.util.Set.class, type)) {
                    if (defaultValue instanceof java.util.Set) {
                        return defaultValue;
                    }
                    return new java.util.HashSet<>();
                }
            }
            if (java.util.Queue.class.isAssignableFrom(type)) {
                if (java.util.Deque.class.isAssignableFrom(type)) {
                    if (Objects.equals(java.util.Deque.class, type)) {
                        if (defaultValue instanceof java.util.Deque) {
                            return defaultValue;
                        }
                        return new java.util.ArrayDeque<>();
                    }
                }
                if (Objects.equals(java.util.Queue.class, type)) {
                    if (defaultValue instanceof java.util.Queue) {
                        return defaultValue;
                    }
                    return new java.util.PriorityQueue<>();
                }
            }
            if (Objects.equals(java.util.Collection.class, type)) {
                if (defaultValue instanceof java.util.Collection) {
                    return defaultValue;
                }
            }
            if (!type.isInterface()) {
                try {
                    return type.newInstance();
                } catch (Exception ignored) {
                }
            }
        }

        // Map
        if (java.util.Map.class.isAssignableFrom(type)) {
            if (java.util.SortedMap.class.isAssignableFrom(type)) {
                if (java.util.NavigableMap.class.isAssignableFrom(type)) {
                    if (Objects.equals(java.util.NavigableMap.class, type)) {
                        if (defaultValue instanceof java.util.NavigableMap) {
                            return defaultValue;
                        }
                        return new java.util.TreeMap<>();
                    }
                }
                if (Objects.equals(java.util.SortedMap.class, type)) {
                    if (defaultValue instanceof java.util.SortedMap) {
                        return defaultValue;
                    }
                    return new java.util.TreeMap<>();
                }
            }
            if (Objects.equals(java.util.Map.class, type)) {
                if (defaultValue instanceof java.util.Map) {
                    return defaultValue;
                }
                return new java.util.HashMap<>();
            }
            if (!type.isInterface()) {
                try {
                    return type.newInstance();
                } catch (Exception ignored) {
                }
            }
        }

        return null;
    }

    public static Object getPrimitiveValue(Class<?> type, Object defaultValue) {
        if (long.class == type) {
            if (defaultValue instanceof Long) {
                return defaultValue;
            }
            if (defaultValue instanceof Number) {
                return ((Number) defaultValue).longValue();
            }
            if (Objects.nonNull(defaultValue)) {
                try {
                    return Long.parseLong(defaultValue.toString());
                } catch (NumberFormatException ignored) {
                }
            }
            return 0L;
        }
        if (int.class == type) {
            if (defaultValue instanceof Integer) {
                return defaultValue;
            }
            if (defaultValue instanceof Number) {
                return ((Number) defaultValue).intValue();
            }
            if (Objects.nonNull(defaultValue)) {
                try {
                    return Integer.parseInt(defaultValue.toString());
                } catch (NumberFormatException ignored) {
                }
            }
            return 0;
        }
        if (short.class == type) {
            if (defaultValue instanceof Short) {
                return defaultValue;
            }
            if (defaultValue instanceof Number) {
                return ((Number) defaultValue).shortValue();
            }
            if (Objects.nonNull(defaultValue)) {
                try {
                    return Short.parseShort(defaultValue.toString());
                } catch (NumberFormatException ignored) {
                }
            }
            return (short) 0;
        }
        if (byte.class == type) {
            if (defaultValue instanceof Byte) {
                return defaultValue;
            }
            if (defaultValue instanceof Number) {
                return ((Number) defaultValue).byteValue();
            }
            if (Objects.nonNull(defaultValue)) {
                try {
                    return Byte.parseByte(defaultValue.toString());
                } catch (NumberFormatException ignored) {
                }
            }
            return (byte) 0;
        }
        if (double.class == type) {
            if (defaultValue instanceof Double) {
                return defaultValue;
            }
            if (defaultValue instanceof Number) {
                return ((Number) defaultValue).doubleValue();
            }
            if (Objects.nonNull(defaultValue)) {
                try {
                    return Double.parseDouble(defaultValue.toString());
                } catch (NumberFormatException ignored) {
                }
            }
            return 0.0;
        }
        if (float.class == type) {
            if (defaultValue instanceof Float) {
                return defaultValue;
            }
            if (defaultValue instanceof Number) {
                return ((Number) defaultValue).floatValue();
            }
            if (Objects.nonNull(defaultValue)) {
                try {
                    return Float.parseFloat(defaultValue.toString());
                } catch (NumberFormatException ignored) {
                }
            }
            return 0.0F;
        }
        if (char.class == type) {
            if (defaultValue instanceof Character) {
                return defaultValue;
            }
            if (Objects.nonNull(defaultValue)) {
                String defaultValueStr = defaultValue.toString();
                if (!defaultValueStr.isEmpty()) {
                    return defaultValueStr.charAt(0);
                }
            }
            return '\0';
        }
        if (boolean.class == type) {
            if (defaultValue instanceof Boolean) {
                return defaultValue;
            }
            if (Objects.nonNull(defaultValue)) {
                return Boolean.parseBoolean(defaultValue.toString());
            }
            return false;
        }
        return null;
    }
}
