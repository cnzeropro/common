package org.zero.common.data.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 通用排序参数。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortQO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 前端字段 key。
	 */
	private String field;

	/**
	 * 排序方向。
	 */
	private Direction direction = Direction.ASC;

	/**
	 * 排序方向：升序 / 降序。
	 */
	public enum Direction {
		ASC,
		DESC
	}
}
