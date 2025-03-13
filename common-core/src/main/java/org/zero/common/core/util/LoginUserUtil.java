package org.zero.common.core.util;

import lombok.experimental.UtilityClass;
import org.zero.common.core.exception.AnyThrow;
import org.zero.common.core.util.shiro.ShiroUtil;
import org.zero.common.core.util.spring.security.SpringSecurityUtil;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
@UtilityClass
public class LoginUserUtil {
    public static Optional<Serializable> getIdOpt() {
        // attempt to obtain from shiro
        Optional<Serializable> userIdOpt = AnyThrow.ignoreOpt(ShiroUtil::getUserId);
        if (userIdOpt.isPresent()) {
            return userIdOpt;
        }
        // attempt to obtain from spring security
        return AnyThrow.ignoreOpt(SpringSecurityUtil::getUserId);
    }

    public static Serializable getId() {
        return getIdOpt().orElse(null);
    }

    public static Optional<String> getNameOpt() {
        // attempt to obtain from shiro
        Optional<String> usernameOpt = AnyThrow.ignoreOpt(ShiroUtil::getUsername);
        if (usernameOpt.isPresent()) {
            return usernameOpt;
        }
        // attempt to obtain from spring security
        return AnyThrow.ignoreOpt(SpringSecurityUtil::getUsername);
    }

    public static String getName() {
        return getNameOpt().orElse(null);
    }
}
