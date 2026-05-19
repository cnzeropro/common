package org.zero.common.core.support.query;

import org.junit.jupiter.api.Test;
import org.zero.common.data.model.query.ListQO;
import org.zero.common.data.model.query.PageQO;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class QueryCompilerTest {
	private final QueryCompiler queryCompiler = new QueryCompiler();

	@Test
	void shouldCompileBusinessQueryObject() {
		UserListQO qo = new UserListQO();
		qo.setNumber(2L);
		qo.setSize(5L);
		qo.setName("tom");

		QuerySpec querySpec = queryCompiler.compile(qo, userListQO -> QuerySpec.of(
				QueryMode.SEARCH,
				AtomicPredicate.of("name", "contains", Collections.<Object>singletonList(userListQO.getName())),
				null,
				Collections.singletonList(SortSpec.of("createdAt", SortDirection.DESC)),
				Arrays.asList("id", "name"),
				Collections.emptyList(),
				Collections.emptyList(),
				PageSpec.of(userListQO.getNumber(), userListQO.getSize())
		));

		assertEquals(QueryMode.SEARCH, querySpec.getMode());
		assertEquals(2L, querySpec.getPage().getNumber());
		assertEquals("name", ((AtomicPredicate) querySpec.getWhere()).getField());
	}

	@Test
	void shouldUseDefaultPageForPlainListQO() {
		ListQO qo = new ListQO();
		qo.setFields(Collections.singletonList("id"));

		QuerySpec querySpec = queryCompiler.compile(qo);

		assertEquals(PageQO.DEFAULT_NUMBER, querySpec.getPage().getNumber());
		assertEquals(PageQO.DEFAULT_SIZE, querySpec.getPage().getSize());
	}

	static class UserListQO extends PageQO {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
