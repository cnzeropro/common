package org.zero.common.data.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 通用报表查询参数。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportQO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 维度字段列表。
	 */
	private List<String> dimensions = Collections.emptyList();

	/**
	 * 指标列表。
	 */
	private List<MetricQO> metrics = Collections.emptyList();

	/**
	 * 查询条件。
	 */
	private FilterQO where;

	/**
	 * 聚合条件。
	 */
	private FilterQO having;

	/**
	 * 排序列表。
	 */
	private List<SortQO> sorts = Collections.emptyList();
}
