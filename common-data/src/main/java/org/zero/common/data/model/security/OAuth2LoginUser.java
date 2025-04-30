package org.zero.common.data.model.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * @author zero
 * @see DefaultOAuth2User
 * @since 2021/8/25
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OAuth2LoginUser extends DefaultOAuth2User implements LoginUser {
    private Serializable id;

    public OAuth2LoginUser(Serializable id, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey) {
        super(authorities, attributes, nameAttributeKey);
        this.id = id;
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttributes(Map<String, Object> attributes) {
        throw new UnsupportedOperationException();
    }
}
