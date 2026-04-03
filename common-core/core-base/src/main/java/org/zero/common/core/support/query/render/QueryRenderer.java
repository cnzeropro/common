package org.zero.common.core.support.query.render;

import org.zero.common.core.support.query.QuerySchema;
import org.zero.common.core.support.query.QuerySpec;

/**
 * 查询渲染器。
 *
 * @param <R> 渲染结果类型
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public interface QueryRenderer<R> {
	R render(QuerySpec querySpec, QuerySchema querySchema);
}
