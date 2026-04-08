package org.zero.common.core.support.query;

import org.junit.jupiter.api.Test;
import org.zero.common.core.support.query.render.SqlFragment;
import org.zero.common.core.support.query.render.SqlQueryRenderer;
import org.zero.common.data.model.query.ConditionGroupQO;
import org.zero.common.data.model.query.ConditionQO;
import org.zero.common.data.model.query.MetricQO;
import org.zero.common.data.model.query.PageQO;
import org.zero.common.data.model.query.QueryQO;
import org.zero.common.data.model.query.ReportQO;
import org.zero.common.data.model.query.SortQO;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
class QuerySupportTest {
	private final QueryCompiler queryCompiler = new QueryCompiler();
	private final OperatorRegistry operatorRegistry = SimpleOperatorRegistry.defaultRegistry();
	private final QuerySchema querySchema = new SimpleQuerySchema("users u", Arrays.asList(
		FieldDescriptor.builder()
			.key("id")
			.javaType(Long.class)
			.columnExpression("u.id")
			.sortable(true)
			.groupable(true)
			.aggregatable(true)
			.supportedOperators(new LinkedHashSet<>(Arrays.asList("eq", "in", "between", "gt", "ge", "lt", "le")))
			.build(),
		FieldDescriptor.builder()
			.key("name")
			.javaType(String.class)
			.columnExpression("u.name")
			.sortable(true)
			.groupable(false)
			.aggregatable(false)
			.supportedOperators(new LinkedHashSet<>(Arrays.asList("eq", "ne", "in", "contains", "startswith", "endswith", "isnull", "isnotnull")))
			.build(),
		FieldDescriptor.builder()
			.key("status")
			.javaType(String.class)
			.columnExpression("u.status")
			.sortable(true)
			.groupable(true)
			.aggregatable(true)
			.supportedOperators(new LinkedHashSet<>(Arrays.asList("eq", "in", "contains")))
			.build(),
		FieldDescriptor.builder()
			.key("createdAt")
			.javaType(LocalDateTime.class)
			.columnExpression("u.created_at")
			.sortable(true)
			.groupable(false)
			.aggregatable(false)
			.supportedOperators(new LinkedHashSet<>(Arrays.asList("eq", "gt", "ge", "lt", "le", "between")))
			.build()
	));

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
	void shouldUseDefaultPageForPlainQueryQO() {
		QueryQO qo = new QueryQO();
		qo.setFields(Collections.singletonList("id"));

		QuerySpec querySpec = queryCompiler.compile(qo);

		assertEquals(PageQO.DEFAULT_NUMBER, querySpec.getPage().getNumber());
		assertEquals(PageQO.DEFAULT_SIZE, querySpec.getPage().getSize());
	}

	@Test
	void shouldRenderSearchSql() {
		LegacyQueryQO qo = new LegacyQueryQO();
		qo.setFields(Arrays.asList("id", "name"));
		qo.setSorts(Collections.singletonList(new SortQO("createdAt", SortQO.Direction.DESC)));
		qo.setWhere(new ConditionGroupQO(ConditionGroupQO.Logic.AND, Arrays.asList(
			new ConditionQO("name", "contains", Collections.<Object>singletonList("tom")),
			new ConditionQO("status", "in", Arrays.<Object>asList("ENABLED", "LOCKED"))
		)));
		qo.setNumber(1L);
		qo.setSize(20L);

		QuerySpec querySpec = queryCompiler.compile(qo);
		SqlFragment sqlFragment = new SqlQueryRenderer(operatorRegistry).render(querySpec, querySchema);

		assertEquals(
			"SELECT u.id AS id, u.name AS name FROM users u WHERE (u.name LIKE ? ESCAPE '\\') AND (u.status IN (?, ?)) ORDER BY u.created_at DESC LIMIT 20 OFFSET 0",
			sqlFragment.getSql()
		);
		assertEquals(Arrays.<Object>asList("%tom%", "ENABLED", "LOCKED"), sqlFragment.getParams());
	}

	@Test
	void shouldRenderReportSql() {
		LegacyReportQO qo = new LegacyReportQO();
		qo.setDimensions(Collections.singletonList("status"));
		qo.setMetrics(Collections.singletonList(new MetricQO("id", "count", "userCount")));
		qo.setWhere(new ConditionQO("status", "in", Collections.<Object>singletonList("ENABLED")));
		qo.setHaving(new ConditionQO("userCount", "gt", Collections.<Object>singletonList(10)));
		qo.setSorts(Collections.singletonList(new SortQO("userCount", SortQO.Direction.DESC)));
		qo.setNumber(2L);
		qo.setSize(10L);

		QuerySpec querySpec = queryCompiler.compile(qo);
		SqlFragment sqlFragment = new SqlQueryRenderer(operatorRegistry).render(querySpec, querySchema);

		assertEquals(
			"SELECT u.status AS status, COUNT(u.id) AS userCount FROM users u WHERE u.status IN (?) GROUP BY u.status HAVING COUNT(u.id) > ? ORDER BY userCount DESC LIMIT 10 OFFSET 10",
			sqlFragment.getSql()
		);
		assertEquals(Arrays.<Object>asList("ENABLED", 10), sqlFragment.getParams());
	}

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

		assertThrows(IllegalArgumentException.class, () -> new QueryValidator(operatorRegistry).validate(querySpec, querySchema));
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

		assertThrows(IllegalArgumentException.class, () -> new QueryValidator(operatorRegistry).validate(querySpec, querySchema));
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

	static class LegacyQueryQO extends QueryQO implements PageParameterProvider {
		private long number = PageQO.DEFAULT_NUMBER;
		private long size = PageQO.DEFAULT_SIZE;

		public long getNumber() {
			return number;
		}

		public void setNumber(long number) {
			this.number = number;
		}

		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}
	}

	static class LegacyReportQO extends ReportQO implements PageParameterProvider {
		private long number = PageQO.DEFAULT_NUMBER;
		private long size = PageQO.DEFAULT_SIZE;

		public long getNumber() {
			return number;
		}

		public void setNumber(long number) {
			this.number = number;
		}

		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}
	}
}
