package org.zero.common.core.support.query;

/**
 * 操作符定义。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public interface OperatorDefinition {
	String code();

	int minValues();

	int maxValues();

	boolean supports(FieldDescriptor fieldDescriptor);

	default void validateValueCount(int count) {
		if (count < minValues()) {
			throw new IllegalArgumentException(String.format("Operator [%s] requires at least %d values", code(), minValues()));
		}
		if (maxValues() >= 0 && count > maxValues()) {
			throw new IllegalArgumentException(String.format("Operator [%s] allows at most %d values", code(), maxValues()));
		}
	}
}
