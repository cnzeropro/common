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
class ConditionQOTest {

	@Test
	void noArgsConstructorShouldUseDefaultOperatorAndValues() {
		ConditionQO conditionQO = new ConditionQO();

		assertAll(
			() -> assertEquals("eq", conditionQO.getOperator()),
			() -> assertTrue(conditionQO.getValues().isEmpty())
		);
	}

	@Test
	void allArgsConstructorShouldKeepProvidedValues() {
		List<Object> values = Arrays.<Object>asList("alice", 18);
		ConditionQO conditionQO = new ConditionQO("name", "in", values);

		assertAll(
			() -> assertEquals("name", conditionQO.getField()),
			() -> assertEquals("in", conditionQO.getOperator()),
			() -> assertSame(values, conditionQO.getValues())
		);
	}
}
