package org.zero.common.core.util.java.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zero
 * @since 2021/4/30
 */
public class ReflectUtil {
    // 访问权限比较器（按访问权限从高到低排序：public → protected → default → private）
    public static final Comparator<Member> ACCESSIBLE_COMPARATOR = Comparator.comparingInt(member -> {
        int mod = member.getModifiers();
        return Modifier.isPublic(mod) ? 1 // public
                : Modifier.isProtected(mod) ? 2 // protected
                : Modifier.isPrivate(mod) ? 4 // private
                : 3; // default
    });

    /**
     * 禁止 Java 的默认访问权限检查机制
     */
    public static <T extends AccessibleObject> T setAccessible(final T accessibleObject) {
        if (Objects.nonNull(accessibleObject) && !accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;
    }

    /**
     * 恢复 Java 的默认访问权限检查机制
     */
    public static <T extends AccessibleObject> T setInaccessible(final T accessibleObject) {
        if (Objects.nonNull(accessibleObject) && accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(false);
        }
        return accessibleObject;
    }

    /**
     * 获取方法或者构造器全限定名
     *
     * @param executable 方法或构造器
     * @return 全限定名
     */
    public static String getFullName(Executable executable) {
        // 类的全限定名
        String className = executable.getDeclaringClass().getName();
        // 方法名
        String executableName = executable.getName();
        // 参数类型列表（全限定名）
        String params = Arrays.stream(executable.getParameterTypes())
                .map(Class::getName)
                .collect(Collectors.joining(", "));
        return String.format("%s.%s(%s)", className, executableName, params);
    }

    public static <T> T getInstance(Class<T> clazz) {
        return getInstanceOpt(clazz).orElse(null);
    }

    /**
     * 获取实例 {@link Optional}
     *
     * @param clazz 类对象
     * @param <T>   类型
     * @return 实例 {@link Optional}
     */
    public static <T> Optional<T> getInstanceOpt(Class<T> clazz) {
        List<Member> members = new ArrayList<>();
        // 从静态构造方法获取
        List<Field> fields = FieldUtil.getStaticFields(clazz, false)
                .stream()
                .filter(field -> Objects.equals(field.getType(), clazz))
                .collect(Collectors.toList());
        members.addAll(fields);
        // 从静态方法获取
        List<Method> methods = MethodUtil.getStaticMethods(clazz, false)
                .stream()
                .filter(MethodUtil::isBuilderMethod)
                .collect(Collectors.toList());
        members.addAll(methods);
        // 从构造器获取
        List<Constructor<?>> constructors = ConstructorUtil.getConstructors(clazz, true);
        members.addAll(constructors);
        // 排序
        members = members.stream()
                // 先按访问权限排序，优先级：public > protected > default > private
                // 再按类型排序，优先级：Field > Method > Constructor
                .sorted(ACCESSIBLE_COMPARATOR.thenComparingInt(member -> member instanceof Field ? 1
                        : member instanceof Method ? 2
                        : member instanceof Constructor ? 3 :
                        4))
                .collect(Collectors.toList());
        for (Member member : members) {
            Object obj = null;
            if (member instanceof Field) {
                obj = FieldUtil.getStaticFieldValue((Field) member);
            } else if (member instanceof Method) {
                Method method = (Method) member;
                Object[] parameters = Arrays.stream(method.getParameterTypes())
                        .map(ClassUtil::getDefaultValue)
                        .toArray();
                obj = MethodUtil.invokeStatic(method, parameters);
            } else if (member instanceof Constructor) {
                Constructor<?> constructor = (Constructor<?>) member;
                Object[] parameters = Arrays.stream(constructor.getParameterTypes())
                        .map(ClassUtil::getDefaultValue)
                        .toArray();
                obj = ConstructorUtil.newInstance(constructor, parameters);
            }
            if (Objects.nonNull(obj)) {
                T instance = ClassUtil.cast(obj, clazz);
                return Optional.ofNullable(instance);
            }
        }
        return Optional.empty();
    }

    protected ReflectUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
