package org.zero.common.core.support.bean.map;

import java.util.Objects;

/**
 * 无需进行下级处理的评估器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/24
 */
@FunctionalInterface
public interface DirectEvaluator {
	/**
	 * 评估一个对象是否要进行下级处理
	 *
	 * @param object 待评估对象
	 * @return 是否要进行下级处理
	 */
	boolean evaluate(Object object);

	default DirectEvaluator and(DirectEvaluator other) {
		Objects.requireNonNull(other);
		return o -> this.evaluate(o) && other.evaluate(o);
	}

	default DirectEvaluator or(DirectEvaluator other) {
		Objects.requireNonNull(other);
		return o -> this.evaluate(o) || other.evaluate(o);
	}

	default DirectEvaluator negate() {
		return o -> !this.evaluate(o);
	}
}
