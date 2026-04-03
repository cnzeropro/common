package org.zero.common.data.model.security;

import java.io.Serializable;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 统一登录主体视图。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/14
 */
public interface LoginUser extends Principal, Serializable {

	/**
	 * 获取用户唯一标识。
	 *
	 * @return 用户 ID
	 */
	Serializable getId();

	/**
	 * 获取用户名称。
	 *
	 * @return 用户名
	 */
	@Override
	String getName();

	/**
	 * 获取用户属性映射。
	 *
	 * @return 属性键值对，可能为 {@code null}
	 */
	Map<String, Object> getAttributes();

	/**
	 * 按键获取单个属性值。
	 *
	 * @param key 属性键
	 * @return 属性值的 {@link Optional}，键不存在时返回 {@link Optional#empty()}
	 */
	default Optional<Object> getAttributeOpt(String key) {
		Map<String, Object> attributes = this.getAttributes();
		if (Objects.isNull(attributes)) {
			return Optional.empty();
		}
		return Optional.ofNullable(attributes.get(key));
	}

	/**
	 * 按键获取单个属性值并转为指定类型。
	 *
	 * @param key  属性键
	 * @param type 目标类型
	 * @param <T>  目标类型泛型
	 * @return 类型转换后的属性值，键不存在时返回 {@link Optional#empty()}
	 */
	default <T> Optional<T> getAttributeOpt(String key, Class<T> type) {
		return this.getAttributeOpt(key).map(type::cast);
	}
}
