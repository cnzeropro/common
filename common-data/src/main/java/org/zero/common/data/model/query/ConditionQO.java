package org.zero.common.data.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 单个条件节点。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConditionQO implements PredicateQO {
	private static final long serialVersionUID = 1L;

	/**
	 * 前端字段 key。
	 */
	private String field;

	/**
	 * 操作符 code，例如 eq / in / contains。
	 */
	private String operator = "eq";

	/**
	 * 条件值列表。
	 */
	private List<Object> values = Collections.emptyList();
}
