package org.zero.common.core.util.shiro;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.zero.common.data.model.security.ShiroLoginUser;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author zero
 * @since 2021/8/22
 */
@Slf4j
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
            log.warn("Failed to get user info", e);
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
