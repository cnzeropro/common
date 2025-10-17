package org.zero.common.core.support.bean.map;

import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Bean 属性
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/24
 */
@Getter
public class BeanProperty {
	private final Object bean;
	private final Field field;
	private final Method getterMethod;

	protected BeanProperty(Object bean, Field field, Method getterMethod) {
		this.bean = bean;
		this.field = field;
		this.getterMethod = getterMethod;
	}
}
