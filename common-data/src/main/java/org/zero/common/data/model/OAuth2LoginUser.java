package org.zero.common.data.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zero
 * @since 2021/8/25
 */
@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OAuth2LoginUser extends SecurityLoginUser implements OAuth2AuthenticatedPrincipal {
    @Setter(AccessLevel.NONE)
    private Map<String, Object> attributes = new HashMap<>();

    public OAuth2LoginUser(Long userId, String username, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        super(userId, username, password, authorities);
    }

    public OAuth2LoginUser(Long userId, String username, String password,
                           boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
                           Collection<? extends GrantedAuthority> authorities) {
        super(userId, username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getName() {
        return this.getUsername();
    }
}
