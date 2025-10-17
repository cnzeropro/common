package org.zero.common.core.support.bean.map;

import cn.hutool.core.bean.BeanUtil;

import java.util.Objects;

/**
 * 默认的 Bean 评估器
 *
 * @author Zero (cnzeropro@163.com)
 * @see BeanUtil#isBean(Class)
 * @since 2025/6/24
 */
public class DefaultBeanEvaluator implements BeanEvaluator {
	public static final DefaultBeanEvaluator INSTANCE = new DefaultBeanEvaluator();

	@Override
	public boolean evaluate(Object object) {
		if (Objects.isNull(object)) {
			return false;
		}
		return BeanUtil.isBean(object.getClass());
	}
}
