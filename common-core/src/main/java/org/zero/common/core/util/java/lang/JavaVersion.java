package org.zero.common.core.util.java.lang;

import lombok.Getter;
import org.zero.common.core.util.java.reflect.FieldUtil;
import org.zero.common.core.util.java.reflect.MethodUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/29
 */
@Getter
public enum JavaVersion {
    /**
     * Java 1.0
     *
     * @since 1.0
     */
    ZERO("1.0", JudgmentStrategy.CLASS, "java.lang.Object"),
    /**
     * Java 1.1
     *
     * @since 1.1
     */
    ONE("1.1", JudgmentStrategy.METHOD, "java.lang.Class", "isPrimitive"),
    /**
     * Java 1.2
     *
     * @since 1.2
     */
    TWO("1.2", JudgmentStrategy.METHOD, "java.lang.Thread", "getContextClassLoader"),
    /**
     * Java 1.3
     *
     * @since 1.3
     */
    THREE("1.3", JudgmentStrategy.METHOD, "java.io.ObjectInputStream", "readClassDescriptor"),
    /**
     * Java 1.4
     *
     * @since 1.4
     */
    FOUR("1.4", JudgmentStrategy.METHOD, "java.io.File", "toURI"),
    /**
     * Java 1.5
     *
     * @since 1.5
     */
    FIVE("1.5", JudgmentStrategy.CLASS, "java.lang.Iterable"),
    /**
     * Java 1.6
     *
     * @since 1.6
     */
    SIX("1.6", JudgmentStrategy.METHOD, "java.lang.String", "isEmpty"),
    /**
     * Java 1.7
     *
     * @since 1.7
     */
    SEVEN("1.7", JudgmentStrategy.CLASS, "java.lang.AutoCloseable"),
    /**
     * Java 1.8
     *
     * @since 1.8
     */
    EIGHT("1.8", JudgmentStrategy.CLASS, "java.util.Optional"),
    /**
     * Java 9
     *
     * @since 9
     */
    NINE("9", JudgmentStrategy.METHOD, "java.util.Optional", "stream"),
    /**
     * Java 10
     *
     * @since 10
     */
    TEN("10", JudgmentStrategy.METHOD, "java.util.Optional", "orElseThrow"),
    /**
     * Java 11
     *
     * @since 11
     */
    ELEVEN("11", JudgmentStrategy.METHOD, "java.lang.String", "strip"),
    /**
     * Java 12
     *
     * @since 12
     */
    TWELVE("12", JudgmentStrategy.METHOD, "java.lang.String", "describeConstable"),
    /**
     * Java 13
     *
     * @since 13
     */
    THIRTEEN("13", JudgmentStrategy.METHOD, "java.nio.file.FileSystems", "newFileSystem", "java.nio.file.Path"),
    /**
     * Java 14
     *
     * @since 14
     */
    FOURTEEN("14", JudgmentStrategy.METHOD, "java.util.concurrent.locks.LockSupport", "setCurrentBlocker", "java.lang.Object"),
    /**
     * Java 15
     *
     * @since 15
     */
    FIFTEEN("15", JudgmentStrategy.METHOD, "java.lang.CharSequence", "isEmpty"),
    /**
     * Java 16
     *
     * @since 16
     */
    SIXTEEN("16", JudgmentStrategy.METHOD, "java.util.stream.Stream", "toList"),
    /**
     * Java 17
     *
     * @since 17
     */
    SEVENTEEN("17", JudgmentStrategy.METHOD, "java.io.Console", "charset"),
    /**
     * Java 18
     *
     * @since 18
     */
    EIGHTEEN("18", JudgmentStrategy.METHOD, "java.time.Duration", "isPositive"),
    /**
     * Java 19
     *
     * @since 19
     */
    NINETEEN("19", JudgmentStrategy.METHOD, "java.lang.Thread", "threadId"),
    /**
     * Java 20
     *
     * @since 20
     */
    TWENTY("20", JudgmentStrategy.METHOD, "java.lang.Class", "accessFlags"),
    /**
     * Java 21
     *
     * @since 21
     */
    TWENTY_ONE("21", JudgmentStrategy.METHOD, "java.util.Deque", "reversed"),
    /**
     * Java 22
     *
     * @since 22
     */
    TWENTY_TWO("22", JudgmentStrategy.METHOD, "java.io.Console", "isTerminal"),
    /**
     * Java 23
     *
     * @since 23
     */
    TWENTY_THREE("23", JudgmentStrategy.METHOD, "java.text.DecimalFormat", "isStrict"),
    ;

    private final String name;

    private final boolean available;

    JavaVersion(String name,
                JudgmentStrategy judgmentStrategy,
                CharSequence className) {
        this(name, judgmentStrategy, className, null);
    }

    JavaVersion(String name,
                JudgmentStrategy judgmentStrategy,
                CharSequence className, CharSequence memberName, CharSequence... typeNames) {
        this.name = name;
        if (judgmentStrategy == JudgmentStrategy.CLASS) {
            this.available = ClassUtil.has(className);
        } else if (judgmentStrategy == JudgmentStrategy.METHOD) {
            this.available = ClassLoaderUtil.loadClassOpt(className)
                    .map(clazz -> {
                        if (Objects.nonNull(typeNames)) {
                            Class<?>[] argTypes = ClassLoaderUtil.loadClass(Arrays.asList(typeNames))
                                    .values()
                                    .toArray(new Class[0]);
                            return MethodUtil.has(clazz, memberName, argTypes);
                        }
                        return MethodUtil.has(clazz, memberName);
                    })
                    .orElse(Boolean.FALSE);
        } else if (judgmentStrategy == JudgmentStrategy.FIELD) {
            this.available = ClassLoaderUtil.loadClassOpt(className)
                    .map(clazz -> FieldUtil.has(clazz, memberName))
                    .orElse(Boolean.FALSE);
        } else {
            this.available = false;
        }
    }

    public static JavaVersion getCurrent() {
        return Arrays.stream(values())
                .sorted(Comparator.reverseOrder())
                .filter(JavaVersion::isAvailable)
                .findFirst()
                .orElse(null);
    }

    public boolean isEqual(JavaVersion version) {
        return this.compareTo(version) == 0;
    }

    public boolean isNewerThan(JavaVersion version) {
        return this.compareTo(version) > 0;
    }

    public boolean isOlderThan(JavaVersion version) {
        return this.compareTo(version) < 0;
    }

    public boolean isEqualOrNewerThan(JavaVersion version) {
        return this.compareTo(version) >= 0;
    }

    public boolean isEqualOrOlderThan(JavaVersion version) {
        return this.compareTo(version) <= 0;
    }

    protected enum JudgmentStrategy {
        CLASS,
        METHOD,
        FIELD,
        ;
    }
}
