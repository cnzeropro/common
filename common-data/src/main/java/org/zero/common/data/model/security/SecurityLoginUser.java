package org.zero.common.data.model.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Spring Security 登录主体。
 *
 * @author zero
 * @see User
 * @since 2019/2/10
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SecurityLoginUser extends User implements LoginUser {

	/**
	 * 用户唯一标识
	 */
	private final Serializable id;

	/**
	 * 扩展属性映射
	 */
	private final Map<String, Object> attributes;

	/**
	 * 构造 Spring Security 登录主体（默认启用、未过期）。
	 *
	 * @param id          用户唯一标识
	 * @param username    用户名
	 * @param password    密码
	 * @param authorities 权限集合
	 */
	public SecurityLoginUser(Serializable id, String username, String password,
	                         Collection<? extends GrantedAuthority> authorities) {
		this(id, username, password, new LinkedHashMap<>(), authorities);
	}

	/**
	 * 构造 Spring Security 登录主体（默认启用、未过期，带扩展属性）。
	 *
	 * @param id          用户唯一标识
	 * @param username    用户名
	 * @param password    密码
	 * @param attributes  扩展属性
	 * @param authorities 权限集合
	 */
	public SecurityLoginUser(Serializable id, String username, String password, Map<String, Object> attributes,
	                         Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.id = id;
		this.attributes = attributes;
	}

	/**
	 * 构造 Spring Security 登录主体（完整账号状态）。
	 *
	 * @param id                    用户唯一标识
	 * @param username              用户名
	 * @param password              密码
	 * @param enabled               是否启用
	 * @param accountNonExpired     账号是否未过期
	 * @param credentialsNonExpired 凭证是否未过期
	 * @param accountNonLocked      账号是否未锁定
	 * @param authorities           权限集合
	 */
	public SecurityLoginUser(Serializable id, String username, String password, boolean enabled,
	                         boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
	                         Collection<? extends GrantedAuthority> authorities) {
		this(id, username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
			new LinkedHashMap<>(), authorities);
	}

	/**
	 * 构造 Spring Security 登录主体（完整账号状态，带扩展属性）。
	 *
	 * @param id                    用户唯一标识
	 * @param username              用户名
	 * @param password              密码
	 * @param enabled               是否启用
	 * @param accountNonExpired     账号是否未过期
	 * @param credentialsNonExpired 凭证是否未过期
	 * @param accountNonLocked      账号是否未锁定
	 * @param attributes            扩展属性
	 * @param authorities           权限集合
	 */
	public SecurityLoginUser(Serializable id, String username, String password, boolean enabled,
	                         boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Map<String, Object> attributes,
	                         Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.id = id;
		this.attributes = attributes;
	}

	/**
	 * 获取用户名称，等同于用户名。
	 *
	 * @return 用户名
	 */
	@Override
	public String getName() {
		return this.getUsername();
	}
}
