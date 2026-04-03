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
class QueryQOTest {

	@Test
	void noArgsConstructorShouldUseEmptyCollections() {
		QueryQO queryQO = new QueryQO();

		assertAll(
			() -> assertNull(queryQO.getWhere()),
			() -> assertTrue(queryQO.getSorts().isEmpty()),
			() -> assertTrue(queryQO.getFields().isEmpty())
		);
	}

	@Test
	void allArgsConstructorShouldKeepProvidedWhereSortsAndFields() {
		PredicateQO where = new ConditionQO("status", "eq", Arrays.<Object>asList("ACTIVE"));
		List<SortQO> sorts = Arrays.asList(new SortQO("createdAt", SortQO.Direction.DESC));
		List<String> fields = Arrays.asList("id", "name");
		QueryQO queryQO = new QueryQO(where, sorts, fields);

		assertAll(
			() -> assertSame(where, queryQO.getWhere()),
			() -> assertSame(sorts, queryQO.getSorts()),
			() -> assertSame(fields, queryQO.getFields())
		);
	}
}
