package org.zero.common.core.support.query.render.sql;

import org.zero.common.core.support.query.FieldDescriptor;
import org.zero.common.core.support.query.render.OperatorHandler;
import org.zero.common.core.support.query.render.SqlFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * BETWEEN SQL 处理器。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public class BetweenSqlOperatorHandler implements OperatorHandler<SqlFragment> {
	@Override
	public String operatorCode() {
		return "between";
	}

	@Override
	public SqlFragment render(FieldDescriptor fieldDescriptor, List<Object> values) {
		return new SqlFragment(
			fieldDescriptor.getColumnExpression() + " BETWEEN ? AND ?",
			Collections.unmodifiableList(new ArrayList<>(values))
		);
	}
}
