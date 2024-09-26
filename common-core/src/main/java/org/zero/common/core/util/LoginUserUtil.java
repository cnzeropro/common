package org.zero.common.core.util;

import lombok.experimental.UtilityClass;
import org.zero.common.core.util.shiro.ShiroUtil;
import org.zero.common.core.util.spring.security.SpringSecurityUtil;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
@UtilityClass
public class LoginUserUtil {
    public static Optional<Serializable> getIdOpt() {
        Serializable userId = null;
        // attempt to obtain from shiro
        try {
            userId = ShiroUtil.getUserId();
        } catch (Exception ignored) {
        }
        if (Objects.nonNull(userId)) {
            return Optional.of(userId);
        }
        // attempt to obtain from spring security
        try {
            userId = SpringSecurityUtil.getUserId();
        } catch (Exception ignored) {
        }
        if (Objects.nonNull(userId)) {
            return Optional.of(userId);
        }
        return Optional.empty();
    }

    public static Serializable getId() {
        return getIdOpt().orElse(null);
    }

    public static Optional<String> getNameOpt() {
        String username = null;
        // attempt to obtain from shiro
        try {
            username = ShiroUtil.getUsername();
        } catch (Exception ignored) {
        }
        if (Objects.nonNull(username)) {
            return Optional.of(username);
        }
        // attempt to obtain from spring security
        try {
            username = SpringSecurityUtil.getUsername();
        } catch (Exception ignored) {
        }
        if (Objects.nonNull(username)) {
            return Optional.of(username);
        }
        return Optional.empty();
    }

    public static String getName() {
        return getNameOpt().orElse(null);
    }
}
