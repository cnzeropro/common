package org.zero.common.core.support.api.crypto.converter;

import org.zero.common.core.extension.java.lang.reflect.TypeReference;
import org.zero.common.core.util.java.lang.ClassUtil;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/12
 */
public interface InputConverter<T> {
	byte[] toBytes(T source);

	/**
	 * 获取支持的类型
	 * <p>
	 * 其本质就是实现类中定义的泛型参数，只不过由于 Java 的泛型擦除，限制太大，只能简单尝试，实际不一定准确，所以建议重写
	 *
	 * @return 支持的类型
	 * @see TypeReference#getTypeArguments(Type)
	 */
	default Type[] supportTypes() {
		return TypeReference.getTypeArguments(this.getClass());
	}

	/**
	 * 判断是否支持转换
	 * <p>
	 * 原因和 {@link #supportTypes()} 一致，因此建议重写
	 *
	 * @param object 待判断对象
	 * @return 是否支持转换
	 */
	default boolean supports(Object object) {
		if (Objects.isNull(object)) {
			return false;
		}
		Class<?> clazz = object.getClass();
		for (Type supportType : this.supportTypes()) {
			if (ClassUtil.isAssignable(ClassUtil.getRawClass(supportType), clazz)) {
				return true;
			}
		}
		return false;
	}
}
