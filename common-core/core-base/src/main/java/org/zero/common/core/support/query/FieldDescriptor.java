package org.zero.common.core.support.query;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 字段描述信息。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
@Getter
public class FieldDescriptor {
	private final String key;
	private final Class<?> javaType;
	private final String columnExpression;
	private final boolean sortable;
	private final boolean groupable;
	private final boolean aggregatable;
	private final Set<String> supportedOperators;

	@Builder
	public FieldDescriptor(String key,
	                       Class<?> javaType,
	                       String columnExpression,
	                       boolean sortable,
	                       boolean groupable,
	                       boolean aggregatable,
	                       Set<String> supportedOperators) {
		this.key = key;
		this.javaType = javaType;
		this.columnExpression = columnExpression;
		this.sortable = sortable;
		this.groupable = groupable;
		this.aggregatable = aggregatable;
		this.supportedOperators = supportedOperators == null
			? Collections.emptySet()
			: Collections.unmodifiableSet(new LinkedHashSet<>(supportedOperators));
	}

	public boolean supportsOperator(String operatorCode) {
		return operatorCode != null && (supportedOperators.isEmpty() || supportedOperators.contains(operatorCode));
	}
}
