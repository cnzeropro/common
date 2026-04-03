package org.zero.common.core.support.query;

import org.zero.common.data.model.query.PageQO;

/**
 * 业务查询对象适配器。
 *
 * @param <T> 业务查询对象类型
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public interface BusinessQueryAdapter<T extends PageQO> {
	QuerySpec adapt(T queryObject);
}
