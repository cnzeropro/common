package org.zero.common.data.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 过滤条件组合节点。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterGroupQO implements FilterQO {
	private static final long serialVersionUID = 1L;

	/**
	 * 逻辑运算。
	 */
	private Logic logic = Logic.AND;

	/**
	 * 子过滤节点。
	 */
	private List<FilterQO> children = Collections.emptyList();

	/**
	 * 逻辑运算符：AND / OR。
	 */
	public enum Logic {
		AND,
		OR
	}
}
