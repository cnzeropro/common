package org.zero.common.data.model.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro 登录主体。
 *
 * @author zero
 * @since 2021/8/22
 */
@Getter
@EqualsAndHashCode
@ToString
public class ShiroLoginUser implements LoginUser {

	/**
	 * 用户唯一标识
	 */
	private final Serializable id;

	/**
	 * 用户名称
	 */
	private final String name;

	/**
	 * 扩展属性映射
	 */
	private final Map<String, Object> attributes;

	/**
	 * 构造 Shiro 登录主体（无扩展属性）。
	 *
	 * @param id   用户唯一标识
	 * @param name 用户名称
	 */
	public ShiroLoginUser(Serializable id, String name) {
		this(id, name, new LinkedHashMap<>());
	}

	/**
	 * 构造 Shiro 登录主体。
	 *
	 * @param id         用户唯一标识
	 * @param name       用户名称
	 * @param attributes 扩展属性
	 */
	public ShiroLoginUser(Serializable id, String name, Map<String, Object> attributes) {
		this.id = id;
		this.name = name;
		this.attributes = attributes;
	}
}
