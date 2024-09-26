package org.zero.common.core.util.spring.security;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zero.common.data.model.SecurityLoginUser;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security 工具类
 */
@Slf4j
@UtilityClass
public class SpringSecurityUtil {
    public static final String ROLE_PREFIX = "ROLE_";

    /**
     * 获取 Authentication
     */
    public Authentication getAuthentication() {
        return getAuthenticationOpt().orElse(null);
    }

    /**
     * 获取 Authentication Optional
     */
    public Optional<Authentication> getAuthenticationOpt() {
        return Optional.ofNullable(SecurityContextHolder.getContext()).map(SecurityContext::getAuthentication);
    }

    /**
     * 获取当前登录用户 Optional
     */
    public Optional<SecurityLoginUser> getUserOpt() {
        return getAuthenticationOpt().map(Authentication::getPrincipal)
                .filter(SecurityLoginUser.class::isInstance)
                .map(SecurityLoginUser.class::cast);
    }

    /**
     * 获取当前登录用户
     */
    public SecurityLoginUser getUser() {
        return getUserOpt().orElse(null);
    }

    /**
     * 获取当前登录用户 ID Optional
     */
    public Optional<Serializable> getUserIdOpt() {
        return getUserOpt().map(SecurityLoginUser::getId);
    }

    /**
     * 获取当前登录用户 ID
     */
    public Serializable getUserId() {
        return getUserIdOpt().orElse(null);
    }

    /**
     * 获取当前登录用户名 Optional
     */
    public Optional<String> getUsernameOpt() {
        return getAuthenticationOpt().map(Authentication::getName);
    }

    /**
     * 获取当前登录用户名
     */
    public String getUsername() {
        return getUsernameOpt().orElse(null);
    }

    /**
     * 获取当前登录用户角色信息
     */
    public Set<String> getRoles() {
        return getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(ROLE_PREFIX))
                .map(authority -> authority.substring(ROLE_PREFIX.length()))
                .collect(Collectors.toSet());
    }

    /**
     * 获取当前登录用户权限信息
     */
    public Set<String> getPermissions() {
        return getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> !authority.startsWith(ROLE_PREFIX))
                .collect(Collectors.toSet());
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthenticationOpt().map(Authentication::getAuthorities)
                .orElseGet(Collections::emptyList);
    }
}
