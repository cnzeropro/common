package org.zero.common.data.model.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * OAuth2 登录主体。
 *
 * @author zero
 * @see DefaultOAuth2User
 * @since 2021/8/25
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OAuth2LoginUser extends DefaultOAuth2User implements LoginUser {

	/**
	 * 用户唯一标识
	 */
	private final Serializable id;

	/**
	 * 构造 OAuth2 登录主体。
	 *
	 * @param id               用户唯一标识
	 * @param authorities      权限集合
	 * @param attributes       OAuth2 用户属性
	 * @param nameAttributeKey 用于提取用户名的属性键
	 */
	public OAuth2LoginUser(Serializable id, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey) {
		super(authorities, attributes, nameAttributeKey);
		this.id = id;
	}
}
