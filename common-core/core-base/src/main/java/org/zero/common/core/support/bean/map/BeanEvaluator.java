package org.zero.common.core.support.bean.map;

import java.util.Objects;

/**
 * Bean 评估器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/24
 */
@FunctionalInterface
public interface BeanEvaluator {
	/**
	 * 评估一个对象是否是 Bean
	 *
	 * @param object 待评估对象
	 * @return 是否是 Bean
	 */
	boolean evaluate(Object object);

	default BeanEvaluator and(BeanEvaluator other) {
		Objects.requireNonNull(other);
		return o -> this.evaluate(o) && other.evaluate(o);
	}

	default BeanEvaluator or(BeanEvaluator other) {
		Objects.requireNonNull(other);
		return o -> this.evaluate(o) || other.evaluate(o);
	}

	default BeanEvaluator negate() {
		return o -> !this.evaluate(o);
	}
}
