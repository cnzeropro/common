package org.zero.common.core.support.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一指标规范。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class MetricSpec implements Serializable {
	private static final long serialVersionUID = 1L;

	private String field;
	private AggregateFunction function;
	private String alias;
}
