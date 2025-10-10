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
public enum IndexType {
	STATISTIC(DatabaseMetaData.tableIndexStatistic),
	CLUSTERED(DatabaseMetaData.tableIndexClustered),
	HASHED(DatabaseMetaData.tableIndexHashed),
	OTHER(DatabaseMetaData.tableIndexOther);

	private final short value;

	public static IndexType from(Short value) {
		for (IndexType type : IndexType.values()) {
			if (Objects.equals(type.value, value)) {
				return type;
			}
		}
		return null;
	}
}
