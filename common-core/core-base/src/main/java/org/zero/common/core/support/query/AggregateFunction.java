package org.zero.common.core.support.query;

import java.util.Locale;

/**
 * 聚合函数。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public enum AggregateFunction {
	COUNT,
	SUM,
	AVG,
	MIN,
	MAX;

	public static AggregateFunction fromCode(String code) {
		if (code == null || code.trim().isEmpty()) {
			throw new IllegalArgumentException("Aggregate function must not be blank");
		}
		String normalizedCode = code.trim().toUpperCase(Locale.ENGLISH);
		for (AggregateFunction function : values()) {
			if (function.name().equals(normalizedCode)) {
				return function;
			}
		}
		throw new IllegalArgumentException(String.format("Unsupported aggregate function: %s", code));
	}
}
