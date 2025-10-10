package org.zero.common.core.util.java.sql;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.DatabaseMetaData;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/19
 */
@Getter
@RequiredArgsConstructor
public enum NullableType {
	NULL(DatabaseMetaData.columnNullable),
	NOT_NULL(DatabaseMetaData.columnNoNulls),
	UNKNOWN(DatabaseMetaData.columnNullableUnknown);

	private final int value;

	public static NullableType from(Integer value) {
		for (NullableType type : NullableType.values()) {
			if (Objects.equals(type.value, value)) {
				return type;
			}
		}
		return null;
	}
}
