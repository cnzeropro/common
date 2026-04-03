package org.zero.common.data.model.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.io.Serializable;
import java.util.Collection;

/**
 * OIDC 登录主体。
 *
 * @author Zero (cnzeropro@163.com)
 * @see DefaultOidcUser
 * @since 2024/11/29
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OidcLoginUser extends DefaultOidcUser implements LoginUser {

	/**
	 * 用户唯一标识
	 */
	private final Serializable id;

	/**
	 * 构造 OIDC 登录主体。
	 *
	 * @param id          用户唯一标识
	 * @param authorities 权限集合
	 * @param idToken     OIDC ID Token
	 */
	public OidcLoginUser(Serializable id, Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken) {
		super(authorities, idToken);
		this.id = id;
	}

	/**
	 * 构造 OIDC 登录主体。
	 *
	 * @param id               用户唯一标识
	 * @param authorities      权限集合
	 * @param idToken          OIDC ID Token
	 * @param nameAttributeKey 用于提取用户名的属性键
	 */
	public OidcLoginUser(Serializable id, Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken,
	                     String nameAttributeKey) {
		super(authorities, idToken, nameAttributeKey);
		this.id = id;
	}

	/**
	 * 构造 OIDC 登录主体。
	 *
	 * @param id          用户唯一标识
	 * @param authorities 权限集合
	 * @param idToken     OIDC ID Token
	 * @param userInfo    OIDC 用户信息
	 */
	public OidcLoginUser(Serializable id, Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken,
	                     OidcUserInfo userInfo) {
		super(authorities, idToken, userInfo);
		this.id = id;
	}

	/**
	 * 构造 OIDC 登录主体。
	 *
	 * @param id               用户唯一标识
	 * @param authorities      权限集合
	 * @param idToken          OIDC ID Token
	 * @param userInfo         OIDC 用户信息
	 * @param nameAttributeKey 用于提取用户名的属性键
	 */
	public OidcLoginUser(Serializable id, Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken,
	                     OidcUserInfo userInfo, String nameAttributeKey) {
		super(authorities, idToken, userInfo, nameAttributeKey);
		this.id = id;
	}
}
