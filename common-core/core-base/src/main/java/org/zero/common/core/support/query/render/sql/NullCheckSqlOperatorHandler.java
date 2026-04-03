package org.zero.common.core.support.query.render.sql;

import org.zero.common.core.support.query.FieldDescriptor;
import org.zero.common.core.support.query.render.OperatorHandler;
import org.zero.common.core.support.query.render.SqlFragment;

import java.util.Collections;
import java.util.List;

/**
 * NULL 检查 SQL 处理器。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public class NullCheckSqlOperatorHandler implements OperatorHandler<SqlFragment> {
	private final String operatorCode;
	private final String template;

	public NullCheckSqlOperatorHandler(String operatorCode, String template) {
		this.operatorCode = operatorCode;
		this.template = template;
	}

	@Override
	public String operatorCode() {
		return operatorCode;
	}

	@Override
	public SqlFragment render(FieldDescriptor fieldDescriptor, List<Object> values) {
		return new SqlFragment(String.format(template, fieldDescriptor.getColumnExpression()), Collections.emptyList());
	}
}
