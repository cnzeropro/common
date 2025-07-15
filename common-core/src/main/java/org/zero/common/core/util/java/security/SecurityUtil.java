package org.zero.common.core.util.java.security;

import lombok.experimental.UtilityClass;
import org.zero.common.core.extension.java.util.function.ThrowThrowableSupplier;
import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/14
 */
@UtilityClass
public class SecurityUtil {
    public static final SecurityManager SECURITY_MANAGER = System.getSecurityManager();
    public static final boolean IS_SECURITY_ENABLED = Objects.nonNull(SECURITY_MANAGER);

    public static <T> T doPrivileged(ThrowThrowableSupplier<T> supplier) {
        if (Objects.isNull(SECURITY_MANAGER)) {
            return ThrowableUtil.sneakyThrow(supplier).get();
        }
        return AccessController.doPrivileged((PrivilegedAction<T>) () -> ThrowableUtil.sneakyThrow(supplier).get());
    }
}
