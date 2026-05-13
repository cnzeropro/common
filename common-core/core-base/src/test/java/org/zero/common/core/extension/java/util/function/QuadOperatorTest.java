package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class QuadOperatorTest {
	@Test
	void shouldApplySameTypeOperation() {
		QuadOperator<Integer> operator = (a, b, c, d) -> a + b + c + d;

		assertEquals(10, operator.apply(1, 2, 3, 4));
	}
}
