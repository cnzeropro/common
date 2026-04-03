package org.zero.common.core.support.query.render;

import org.zero.common.core.support.query.FieldDescriptor;

import java.util.List;

/**
 * 操作符渲染处理器。
 *
 * @param <R> 渲染结果类型
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public interface OperatorHandler<R> {
	String operatorCode();

	R render(FieldDescriptor fieldDescriptor, List<Object> values);
}
