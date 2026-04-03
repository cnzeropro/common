package org.zero.common.data.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 通用列表查询参数。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryQO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 查询条件。
	 */
	private PredicateQO where;

	/**
	 * 排序列表。
	 */
	private List<SortQO> sorts = Collections.emptyList();

	/**
	 * 选择字段列表。
	 */
	private List<String> fields = Collections.emptyList();
}
