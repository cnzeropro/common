package org.zero.common.core.util.java.reflect;

import java.lang.reflect.Constructor;
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
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/1
 */
public class MemberUtil {
    // 访问权限比较器（按访问权限从高到低排序：public → protected → default → private）
    public static final Comparator<Member> ACCESSIBLE_COMPARATOR = Comparator.comparingInt(member -> {
        int mod = member.getModifiers();
        return Modifier.isPublic(mod) ? 1 // public
                : Modifier.isProtected(mod) ? 2 // protected
                : Modifier.isPrivate(mod) ? 4 // private
                : 3; // default
    });

    /**
     * 获取实例
     *
     * @param clazz 类对象
     * @param <T>   类型
     * @return 实例
     */
    public static <T> T getInstance(Class<T> clazz) {
        return getInstance(clazz, false);
    }

    /**
     * 获取实例
     *
     * @param clazz            类对象
     * @param quietIfException 是否安静处理（不抛出异常）
     * @param <T>              类型
     * @return 实例
     */
    public static <T> T getInstance(Class<T> clazz, boolean quietIfException) {
        return getInstanceOpt(clazz, quietIfException).orElse(null);
    }

    /**
     * 获取实例 {@link Optional}
     *
     * @param clazz 类对象
     * @param <T>   类型
     * @return 实例 {@link Optional}
     */
    public static <T> Optional<T> getInstanceOpt(Class<T> clazz) {
        return getInstanceOpt(clazz, true);
    }

    /**
     * 获取实例 {@link Optional}
     *
     * @param clazz            类对象
     * @param quietIfException 是否安静处理（不抛出异常）
     * @param <T>              类型
     * @return 实例 {@link Optional}
     */
    public static <T> Optional<T> getInstanceOpt(Class<T> clazz, boolean quietIfException) {
        List<Member> members = new ArrayList<>();
        // 从静态构造方法获取
        List<Field> fields = FieldUtil.getStaticFields(clazz, false)
                .stream()
                .filter(field -> ClassUtil.isAssignable(field.getType(), clazz))
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
                obj = FieldUtil.getStaticFieldValue((Field) member, quietIfException);
            } else if (member instanceof Method) {
                Method method = (Method) member;
                Object[] parameters = Arrays.stream(method.getParameterTypes())
                        .map(ClassUtil::getDefaultValue)
                        .toArray();
                obj = MethodUtil.invokeStatic(method, quietIfException, parameters);
            } else if (member instanceof Constructor) {
                Constructor<?> constructor = (Constructor<?>) member;
                Object[] parameters = Arrays.stream(constructor.getParameterTypes())
                        .map(ClassUtil::getDefaultValue)
                        .toArray();
                obj = ConstructorUtil.newInstance(constructor, quietIfException, parameters);
            }
            if (Objects.nonNull(obj)) {
                T instance = ClassUtil.cast(obj, clazz);
                return Optional.ofNullable(instance);
            }
        }
        return Optional.empty();
    }
}
