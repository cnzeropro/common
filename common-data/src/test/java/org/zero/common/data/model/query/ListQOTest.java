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
class ListQOTest {

	@Test
	void noArgsConstructorShouldUseEmptyCollections() {
		ListQO listQO = new ListQO();

		assertAll(
				() -> assertNull(listQO.getWhere()),
				() -> assertTrue(listQO.getSorts().isEmpty()),
				() -> assertTrue(listQO.getFields().isEmpty())
		);
	}

	@Test
	void allArgsConstructorShouldKeepProvidedWhereSortsAndFields() {
		FilterQO where = new FilterConditionQO("status", "eq", Arrays.<Object>asList("ACTIVE"));
		List<SortQO> sorts = Arrays.asList(new SortQO("createdAt", SortQO.Direction.DESC));
		List<String> fields = Arrays.asList("id", "name");
		ListQO listQO = new ListQO(where, sorts, fields);

		assertAll(
				() -> assertSame(where, listQO.getWhere()),
				() -> assertSame(sorts, listQO.getSorts()),
				() -> assertSame(fields, listQO.getFields())
		);
	}
}
