package org.zero.common.data.model.query;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/3
 */
class FilterConditionQOTest {

	@Test
	void noArgsConstructorShouldUseDefaultOperatorAndValues() {
		FilterConditionQO filterConditionQO = new FilterConditionQO();

		assertAll(
				() -> assertEquals("eq", filterConditionQO.getOperator()),
				() -> assertTrue(filterConditionQO.getValues().isEmpty())
		);
	}

	@Test
	void allArgsConstructorShouldKeepProvidedValues() {
		List<Object> values = Arrays.<Object>asList("alice", 18);
		FilterConditionQO filterConditionQO = new FilterConditionQO("name", "in", values);

		assertAll(
				() -> assertEquals("name", filterConditionQO.getField()),
				() -> assertEquals("in", filterConditionQO.getOperator()),
				() -> assertSame(values, filterConditionQO.getValues())
		);
	}
}
