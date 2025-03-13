package org.zero.common.core.util.java.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.function.ToIntFunction;

/**
 * @author zero
 * @since 2021/4/30
 */
public class ReflectUtil {
    public static final ToIntFunction<? super Member> ACCESSIBLE_COMPARATOR = member -> {
        int mod = member.getModifiers();
        // public
        if (Modifier.isPublic(mod)) {
            return 4;
        }
        // protected
        if (Modifier.isProtected(mod)) {
            return 3;
        }
        // default（包私有）
        if (!Modifier.isPublic(mod) &&
                !Modifier.isProtected(mod) &&
                !Modifier.isPrivate(mod)) {
            return 2;
        }
        // private
        if (Modifier.isPrivate(mod)) {
            return 1;
        }
        return 0;
    };

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

    protected ReflectUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
