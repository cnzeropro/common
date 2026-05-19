package org.zero.common.core.support.query.render;

import org.junit.jupiter.api.Test;
import org.zero.common.core.support.query.PageParameterProvider;
import org.zero.common.core.support.query.QueryCompiler;
import org.zero.common.core.support.query.QuerySpec;
import org.zero.common.core.support.query.QueryTestFixtures;
import org.zero.common.data.model.query.FilterConditionQO;
import org.zero.common.data.model.query.FilterGroupQO;
import org.zero.common.data.model.query.ListQO;
import org.zero.common.data.model.query.MetricQO;
import org.zero.common.data.model.query.PageQO;
import org.zero.common.data.model.query.ReportQO;
import org.zero.common.data.model.query.SortQO;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class SqlQueryRendererTest {
	private final QueryCompiler queryCompiler = new QueryCompiler();
	private final SqlQueryRenderer sqlQueryRenderer = new SqlQueryRenderer(QueryTestFixtures.operatorRegistry());

	@Test
	void shouldRenderSearchSql() {
		LegacyListQO qo = new LegacyListQO();
		qo.setFields(Arrays.asList("id", "name"));
		qo.setSorts(Collections.singletonList(new SortQO("createdAt", SortQO.Direction.DESC)));
		qo.setWhere(new FilterGroupQO(FilterGroupQO.Logic.AND, Arrays.asList(
				new FilterConditionQO("name", "contains", Collections.<Object>singletonList("tom")),
				new FilterConditionQO("status", "in", Arrays.<Object>asList("ENABLED", "LOCKED"))
		)));
		qo.setNumber(1L);
		qo.setSize(20L);

		QuerySpec querySpec = queryCompiler.compile(qo);
		SqlFragment sqlFragment = sqlQueryRenderer.render(querySpec, QueryTestFixtures.querySchema());

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
		qo.setWhere(new FilterConditionQO("status", "in", Collections.<Object>singletonList("ENABLED")));
		qo.setHaving(new FilterConditionQO("userCount", "gt", Collections.<Object>singletonList(10)));
		qo.setSorts(Collections.singletonList(new SortQO("userCount", SortQO.Direction.DESC)));
		qo.setNumber(2L);
		qo.setSize(10L);

		QuerySpec querySpec = queryCompiler.compile(qo);
		SqlFragment sqlFragment = sqlQueryRenderer.render(querySpec, QueryTestFixtures.querySchema());

		assertEquals(
				"SELECT u.status AS status, COUNT(u.id) AS userCount FROM users u WHERE u.status IN (?) GROUP BY u.status HAVING COUNT(u.id) > ? ORDER BY userCount DESC LIMIT 10 OFFSET 10",
				sqlFragment.getSql()
		);
		assertEquals(Arrays.<Object>asList("ENABLED", 10), sqlFragment.getParams());
	}

	static class LegacyListQO extends ListQO implements PageParameterProvider {
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
