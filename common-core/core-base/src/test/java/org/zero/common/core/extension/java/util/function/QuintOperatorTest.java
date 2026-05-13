package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class QuintOperatorTest {
	@Test
	void shouldApplySameTypeOperation() {
		QuintOperator<Integer> operator = (a, b, c, d, e) -> a + b + c + d + e;

		assertEquals(15, operator.apply(1, 2, 3, 4, 5));
	}
}
