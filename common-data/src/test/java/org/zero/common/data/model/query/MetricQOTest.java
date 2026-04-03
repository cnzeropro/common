package org.zero.common.data.model.query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/3
 */
class MetricQOTest {

	@Test
	void noArgsConstructorShouldStartWithNullFields() {
		MetricQO metricQO = new MetricQO();

		assertAll(
			() -> assertNull(metricQO.getField()),
			() -> assertNull(metricQO.getFunction()),
			() -> assertNull(metricQO.getAlias())
		);
	}

	@Test
	void allArgsConstructorShouldKeepProvidedMetricMetadata() {
		MetricQO metricQO = new MetricQO("amount", "sum", "totalAmount");

		assertAll(
			() -> assertEquals("amount", metricQO.getField()),
			() -> assertEquals("sum", metricQO.getFunction()),
			() -> assertEquals("totalAmount", metricQO.getAlias())
		);
	}
}
