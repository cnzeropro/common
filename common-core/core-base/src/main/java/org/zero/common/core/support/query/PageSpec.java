package org.zero.common.core.support.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zero.common.data.model.query.PageQO;

import java.io.Serializable;

/**
 * 统一分页规范。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class PageSpec implements Serializable {
	private static final long serialVersionUID = 1L;

	private long number = 1L;
	private long size = PageQO.DEFAULT_SIZE;

	public long getOffset() {
		return Math.max(number - 1L, 0L) * size;
	}
}
