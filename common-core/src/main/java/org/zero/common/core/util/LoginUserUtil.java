package org.zero.common.core.util;

import org.zero.common.core.util.apache.shiro.ShiroUtil;
import org.zero.common.core.util.java.lang.ThrowableUtil;
import org.zero.common.core.util.spring.security.SecurityUtil;
import org.zero.common.data.model.security.LoginUser;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
public class LoginUserUtil {
    public static Optional<LoginUser> getOpt() {
        // attempt to obtain from spring security
        Optional<LoginUser> userOpt = ThrowableUtil.ignoreOpt(SecurityUtil::getUser);
        if (userOpt.isPresent()) {
            return userOpt;
        }
        // attempt to obtain from shiro
        return ThrowableUtil.ignoreOpt(ShiroUtil::getUser);
    }

    public static LoginUser get() {
        return getOpt().orElse(null);
    }

    public static Optional<Serializable> getIdOpt() {
        return getOpt().map(LoginUser::getId);
    }

    public static Serializable getId() {
        return getIdOpt().orElse(null);
    }

    public static Optional<String> getNameOpt() {
        return getOpt().map(LoginUser::getName);
    }

    public static String getName() {
        return getNameOpt().orElse(null);
    }

    protected LoginUserUtil() {
        throw new UnsupportedOperationException();
    }
}
