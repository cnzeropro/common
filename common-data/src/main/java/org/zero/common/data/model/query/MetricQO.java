package org.zero.common.data.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 聚合指标参数。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricQO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 前端字段 key。
	 */
	private String field;

	/**
	 * 聚合函数，例如 count / sum / avg。
	 */
	private String function;

	/**
	 * 指标别名。
	 */
	private String alias;
}
