package org.zero.common.core.support.query;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * 查询支持组件测试夹具。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
public final class QueryTestFixtures {
	private QueryTestFixtures() {
	}

	public static OperatorRegistry operatorRegistry() {
		return SimpleOperatorRegistry.defaultRegistry();
	}

	public static QuerySchema querySchema() {
		return new SimpleQuerySchema("users u", Arrays.asList(
				FieldDescriptor.builder()
						.key("id")
						.javaType(Long.class)
						.columnExpression("u.id")
						.sortable(true)
						.groupable(true)
						.aggregatable(true)
						.supportedOperators(new LinkedHashSet<>(Arrays.asList("eq", "in", "between", "gt", "ge", "lt", "le")))
						.build(),
				FieldDescriptor.builder()
						.key("name")
						.javaType(String.class)
						.columnExpression("u.name")
						.sortable(true)
						.groupable(false)
						.aggregatable(false)
						.supportedOperators(new LinkedHashSet<>(
								Arrays.asList("eq", "ne", "in", "contains", "startswith", "endswith", "isnull", "isnotnull")
						))
						.build(),
				FieldDescriptor.builder()
						.key("status")
						.javaType(String.class)
						.columnExpression("u.status")
						.sortable(true)
						.groupable(true)
						.aggregatable(true)
						.supportedOperators(new LinkedHashSet<>(Arrays.asList("eq", "in", "contains")))
						.build(),
				FieldDescriptor.builder()
						.key("createdAt")
						.javaType(LocalDateTime.class)
						.columnExpression("u.created_at")
						.sortable(true)
						.groupable(false)
						.aggregatable(false)
						.supportedOperators(new LinkedHashSet<>(Arrays.asList("eq", "gt", "ge", "lt", "le", "between")))
						.build()
		));
	}
}
