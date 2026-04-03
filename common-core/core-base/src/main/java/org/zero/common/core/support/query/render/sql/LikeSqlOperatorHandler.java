package org.zero.common.core.support.query.render.sql;

import org.zero.common.core.support.query.FieldDescriptor;
import org.zero.common.core.support.query.render.OperatorHandler;
import org.zero.common.core.support.query.render.SqlFragment;

import java.util.Collections;
import java.util.List;

/**
 * LIKE SQL 处理器。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public class LikeSqlOperatorHandler implements OperatorHandler<SqlFragment> {
	private final String operatorCode;
	private final boolean prefixWildcard;
	private final boolean suffixWildcard;

	public LikeSqlOperatorHandler(String operatorCode, boolean prefixWildcard, boolean suffixWildcard) {
		this.operatorCode = operatorCode;
		this.prefixWildcard = prefixWildcard;
		this.suffixWildcard = suffixWildcard;
	}

	@Override
	public String operatorCode() {
		return operatorCode;
	}

	@Override
	public SqlFragment render(FieldDescriptor fieldDescriptor, List<Object> values) {
		String value = values.get(0) == null ? "" : values.get(0).toString();
		String escaped = value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
		StringBuilder pattern = new StringBuilder();
		if (prefixWildcard) {
			pattern.append('%');
		}
		pattern.append(escaped);
		if (suffixWildcard) {
			pattern.append('%');
		}
		return new SqlFragment(
			fieldDescriptor.getColumnExpression() + " LIKE ? ESCAPE '\\'",
			Collections.<Object>singletonList(pattern.toString())
		);
	}
}
