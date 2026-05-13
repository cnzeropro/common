package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class TriOperatorTest {
	@Test
	void shouldApplySameTypeOperation() {
		TriOperator<Integer> operator = (a, b, c) -> a + b + c;

		assertEquals(6, operator.apply(1, 2, 3));
	}
}
