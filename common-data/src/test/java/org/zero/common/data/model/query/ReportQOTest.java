package org.zero.common.data.model.query;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/3
 */
class ReportQOTest {

	@Test
	void noArgsConstructorShouldUseEmptyCollections() {
		ReportQO reportQO = new ReportQO();

		assertAll(
			() -> assertTrue(reportQO.getDimensions().isEmpty()),
			() -> assertTrue(reportQO.getMetrics().isEmpty()),
			() -> assertNull(reportQO.getWhere()),
			() -> assertNull(reportQO.getHaving()),
			() -> assertTrue(reportQO.getSorts().isEmpty())
		);
	}

	@Test
	void allArgsConstructorShouldKeepProvidedQueryParts() {
		List<String> dimensions = Arrays.asList("dept", "owner");
		List<MetricQO> metrics = Arrays.asList(new MetricQO("amount", "sum", "totalAmount"));
		PredicateQO where = new ConditionQO("status", "eq", Arrays.<Object>asList("PAID"));
		PredicateQO having = new ConditionQO("totalAmount", "gt", Arrays.<Object>asList(100));
		List<SortQO> sorts = Arrays.asList(new SortQO("totalAmount", SortQO.Direction.DESC));
		ReportQO reportQO = new ReportQO(dimensions, metrics, where, having, sorts);

		assertAll(
			() -> assertSame(dimensions, reportQO.getDimensions()),
			() -> assertSame(metrics, reportQO.getMetrics()),
			() -> assertSame(where, reportQO.getWhere()),
			() -> assertSame(having, reportQO.getHaving()),
			() -> assertSame(sorts, reportQO.getSorts())
		);
	}
}
