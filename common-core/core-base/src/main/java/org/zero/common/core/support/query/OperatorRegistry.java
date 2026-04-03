package org.zero.common.core.support.query;

import java.util.Collection;

/**
 * 操作符注册中心。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public interface OperatorRegistry {
	OperatorDefinition getOperator(String code);

	Collection<OperatorDefinition> getOperators();

	default OperatorDefinition require(String code) {
		OperatorDefinition operatorDefinition = getOperator(code);
		if (operatorDefinition == null) {
			throw new IllegalArgumentException(String.format("Unsupported operator: %s", code));
		}
		return operatorDefinition;
	}
}
