package org.zero.common.core.extension.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.zero.common.core.support.query.QueryCompiler;
import org.zero.common.core.support.query.QuerySpec;
import org.zero.common.data.model.query.ConditionGroupQO;
import org.zero.common.data.model.query.ConditionQO;
import org.zero.common.data.model.query.QueryQO;
import org.zero.common.data.model.query.ReportQO;
import org.zero.common.data.model.query.SortQO;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
class QueryQOJacksonConfigurerTest {
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final QueryCompiler queryCompiler = new QueryCompiler();

	QueryQOJacksonConfigurerTest() {
		QueryQOJacksonConfigurer.configure(objectMapper);
	}

	@Test
	void shouldDeserializeQueryQO() throws Exception {
		String json = "{"
			+ "\"number\":1,"
			+ "\"size\":20,"
			+ "\"fields\":[\"id\",\"name\"],"
			+ "\"sorts\":[{\"field\":\"createdAt\",\"direction\":\"desc\"}],"
			+ "\"where\":{"
			+ "\"type\":\"group\","
			+ "\"logic\":\"and\","
			+ "\"children\":["
			+ "{\"type\":\"condition\",\"field\":\"name\",\"operator\":\"contains\",\"values\":[\"tom\"]},"
			+ "{\"type\":\"condition\",\"field\":\"status\",\"operator\":\"in\",\"values\":[\"ENABLED\",\"LOCKED\"]}"
			+ "]"
			+ "}"
			+ "}";

		QueryQO qo = objectMapper.readValue(json, QueryQO.class);
		QuerySpec querySpec = queryCompiler.compile(qo);

		assertEquals(Arrays.asList("id", "name"), qo.getFields());
		assertNotNull(qo.getWhere());
		assertEquals(SortQO.Direction.DESC, qo.getSorts().get(0).getDirection());
		assertInstanceOf(ConditionGroupQO.class, qo.getWhere());
		assertEquals(ConditionGroupQO.Logic.AND, ((ConditionGroupQO) qo.getWhere()).getLogic());
		assertEquals(1L, querySpec.getPage().getNumber());
		assertEquals(20L, querySpec.getPage().getSize());
	}

	@Test
	void shouldDeserializeReportQO() throws Exception {
		String json = "{"
			+ "\"number\":2,"
			+ "\"size\":10,"
			+ "\"dimensions\":[\"status\"],"
			+ "\"metrics\":[{\"field\":\"id\",\"function\":\"count\",\"alias\":\"userCount\"}],"
			+ "\"having\":{\"type\":\"condition\",\"field\":\"userCount\",\"operator\":\"gt\",\"values\":[10]}"
			+ "}";

		ReportQO qo = objectMapper.readValue(json, ReportQO.class);
		QuerySpec querySpec = queryCompiler.compile(qo);

		assertEquals(Collections.singletonList("status"), qo.getDimensions());
		assertInstanceOf(ConditionQO.class, qo.getHaving());
		assertEquals(2L, querySpec.getPage().getNumber());
		assertEquals(10L, querySpec.getPage().getSize());
	}
}
