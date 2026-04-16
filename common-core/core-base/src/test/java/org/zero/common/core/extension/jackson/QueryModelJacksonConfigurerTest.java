package org.zero.common.core.extension.jackson;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.zero.common.core.support.query.QueryCompiler;
import org.zero.common.core.support.query.QuerySpec;
import org.zero.common.data.model.query.FilterConditionQO;
import org.zero.common.data.model.query.FilterGroupQO;
import org.zero.common.data.model.query.FilterQO;
import org.zero.common.data.model.query.ListQO;
import org.zero.common.data.model.query.ReportQO;
import org.zero.common.data.model.query.SortQO;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
class QueryModelJacksonConfigurerTest {
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final QueryCompiler queryCompiler = new QueryCompiler();
	private Jackson2ObjectMapperBuilder builder;

	@BeforeEach
	void setUp() {
		QueryModelJacksonConfigurer.configure(objectMapper);
		builder = new Jackson2ObjectMapperBuilder();
	}

	@Test
	void shouldDeserializeListQO() throws Exception {
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

		ListQO qo = objectMapper.readValue(json, ListQO.class);
		QuerySpec querySpec = queryCompiler.compile(qo);

		assertEquals(Arrays.asList("id", "name"), qo.getFields());
		assertNotNull(qo.getWhere());
		assertEquals(SortQO.Direction.DESC, qo.getSorts().get(0).getDirection());
		assertInstanceOf(FilterGroupQO.class, qo.getWhere());
		assertEquals(FilterGroupQO.Logic.AND, ((FilterGroupQO) qo.getWhere()).getLogic());
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
		assertInstanceOf(FilterConditionQO.class, qo.getHaving());
		assertEquals(2L, querySpec.getPage().getNumber());
		assertEquals(10L, querySpec.getPage().getSize());
	}

	@Test
	void shouldCustomizeBuilder() {
		QueryModelJacksonConfigurer configurer = new QueryModelJacksonConfigurer();

		configurer.customize(builder);

		ObjectMapper configuredObjectMapper = builder.build();

		assertTrue(configuredObjectMapper.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS));
		assertNotNull(configuredObjectMapper.findMixInClassFor(ListQO.class));
		assertNotNull(configuredObjectMapper.findMixInClassFor(ReportQO.class));
		assertNotNull(configuredObjectMapper.findMixInClassFor(FilterQO.class));
	}
}
