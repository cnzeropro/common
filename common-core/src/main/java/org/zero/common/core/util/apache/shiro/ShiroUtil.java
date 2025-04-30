package org.zero.common.core.util.apache.shiro;

import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.zero.common.data.model.security.ShiroLoginUser;

import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Level;

/**
 * @author zero
 * @since 2021/8/22
 */
@Log
@UtilityClass
public class ShiroUtil {
    public Optional<ShiroLoginUser> getUserOptWithEx() {
        return Optional.ofNullable(SecurityUtils.getSubject())
                .map(Subject::getPrincipal)
                .filter(ShiroLoginUser.class::isInstance)
                .map(ShiroLoginUser.class::cast);
    }

    public Optional<ShiroLoginUser> getUserOpt() {
        try {
            return getUserOptWithEx();
        } catch (Exception e) {
            log.log(Level.WARNING, "Failed to get user info from context", e);
            return Optional.empty();
        }
    }

    public ShiroLoginUser getUser() {
        return getUserOpt().orElse(null);
    }

    public Optional<Serializable> getUserIdOpt() {
        return getUserOpt().map(ShiroLoginUser::getId);
    }

    public Serializable getUserId() {
        return getUserIdOpt().orElse(null);
    }

    public Optional<String> getUsernameOpt() {
        return getUserOpt().map(ShiroLoginUser::getName);
    }

    public String getUsername() {
        return getUsernameOpt().orElse(null);
    }
}
