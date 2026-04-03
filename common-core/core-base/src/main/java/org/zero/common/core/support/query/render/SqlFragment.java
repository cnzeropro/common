package org.zero.common.core.support.query.render;

import lombok.Value;

import java.util.Collections;
import java.util.List;

/**
 * SQL 片段。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
@Value
public class SqlFragment {
	String sql;
	List<Object> params;

	public static SqlFragment empty() {
		return new SqlFragment("", Collections.emptyList());
	}

	public boolean isEmpty() {
		return sql == null || sql.trim().isEmpty();
	}
}
