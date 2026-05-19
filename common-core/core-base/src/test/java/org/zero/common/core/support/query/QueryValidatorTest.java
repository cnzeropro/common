package org.zero.common.core.support.query;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class QueryValidatorTest {
	private final QueryValidator queryValidator = new QueryValidator(QueryTestFixtures.operatorRegistry());

	@Test
	void shouldRejectUnsupportedField() {
		QuerySpec querySpec = QuerySpec.of(
				QueryMode.SEARCH,
				AtomicPredicate.of("missing", "eq", Collections.<Object>singletonList(1)),
				null,
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				PageSpec.of(1L, 10L)
		);

		assertThrows(IllegalArgumentException.class, () -> queryValidator.validate(querySpec, QueryTestFixtures.querySchema()));
	}

	@Test
	void shouldRejectUnsupportedOperatorForField() {
		QuerySpec querySpec = QuerySpec.of(
				QueryMode.SEARCH,
				AtomicPredicate.of("createdAt", "contains", Collections.<Object>singletonList("2026")),
				null,
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				PageSpec.of(1L, 10L)
		);

		assertThrows(IllegalArgumentException.class, () -> queryValidator.validate(querySpec, QueryTestFixtures.querySchema()));
	}
}
