package org.zero.common.core.support.query;

/**
 * 查询分页参数提供者。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/8
 */
public interface PageParameterProvider {
	long getNumber();

	long getSize();
}
