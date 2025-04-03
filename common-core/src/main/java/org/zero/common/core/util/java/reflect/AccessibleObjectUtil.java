package org.zero.common.core.util.java.reflect;

import java.lang.reflect.AccessibleObject;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/1
 */
public class AccessibleObjectUtil {
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
}
