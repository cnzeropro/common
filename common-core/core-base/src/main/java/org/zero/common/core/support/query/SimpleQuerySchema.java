package org.zero.common.core.support.query;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 默认查询 schema 实现。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public class SimpleQuerySchema implements QuerySchema {
	private final String fromClause;
	private final Map<String, FieldDescriptor> fieldMap;

	public SimpleQuerySchema(String fromClause, Collection<FieldDescriptor> fieldDescriptors) {
		if (fromClause == null || fromClause.trim().isEmpty()) {
			throw new IllegalArgumentException("From clause must not be blank");
		}
		this.fromClause = fromClause.trim();
		this.fieldMap = new LinkedHashMap<>();
		if (fieldDescriptors != null) {
			for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
				if (fieldDescriptor == null || fieldDescriptor.getKey() == null || fieldDescriptor.getKey().trim().isEmpty()) {
					throw new IllegalArgumentException("Field descriptor key must not be blank");
				}
				fieldMap.put(fieldDescriptor.getKey().trim(), fieldDescriptor);
			}
		}
	}

	@Override
	public String getFromClause() {
		return fromClause;
	}

	@Override
	public FieldDescriptor getField(String key) {
		if (key == null) {
			return null;
		}
		return fieldMap.get(key.trim());
	}

	@Override
	public Collection<FieldDescriptor> getFields() {
		return Collections.unmodifiableCollection(fieldMap.values());
	}
}
