package org.zero.common.core.support.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zero.common.data.model.query.PageQO;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 统一查询规范。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class QuerySpec implements Serializable {
	private static final long serialVersionUID = 1L;

	private QueryMode mode = QueryMode.SEARCH;
	private PredicateNode where;
	private PredicateNode having;
	private List<SortSpec> sorts = Collections.emptyList();
	private List<String> fields = Collections.emptyList();
	private List<String> dimensions = Collections.emptyList();
	private List<MetricSpec> metrics = Collections.emptyList();
	private PageSpec page = PageSpec.of(PageQO.DEFAULT_NUMBER, PageQO.DEFAULT_SIZE);
}
