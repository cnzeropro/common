package org.zero.common.core.support.query;

import lombok.Builder;

import java.util.function.Predicate;

/**
 * 默认操作符定义实现。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public class SimpleOperatorDefinition implements OperatorDefinition {
	private final String code;
	private final int minValues;
	private final int maxValues;
	private final Predicate<FieldDescriptor> supportPredicate;

	@Builder
	public SimpleOperatorDefinition(String code, int minValues, int maxValues, Predicate<FieldDescriptor> supportPredicate) {
		if (code == null || code.trim().isEmpty()) {
			throw new IllegalArgumentException("Operator code must not be blank");
		}
		this.code = code.trim();
		this.minValues = minValues;
		this.maxValues = maxValues;
		this.supportPredicate = supportPredicate == null ? fieldDescriptor -> true : supportPredicate;
	}

	@Override
	public String code() {
		return code;
	}

	@Override
	public int minValues() {
		return minValues;
	}

	@Override
	public int maxValues() {
		return maxValues;
	}

	@Override
	public boolean supports(FieldDescriptor fieldDescriptor) {
		return supportPredicate.test(fieldDescriptor);
	}
}
