package org.zero.common.core.support.query;

import java.util.Collection;

/**
 * 查询字段白名单与数据源描述。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public interface QuerySchema {
	String getFromClause();

	FieldDescriptor getField(String key);

	Collection<FieldDescriptor> getFields();

	default FieldDescriptor requireField(String key) {
		FieldDescriptor fieldDescriptor = getField(key);
		if (fieldDescriptor == null) {
			throw new IllegalArgumentException(String.format("Unsupported field: %s", key));
		}
		return fieldDescriptor;
	}
}
