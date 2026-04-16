package org.zero.common.core.extension.jackson;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;
import org.zero.common.core.extension.jackson.databind.deser.HutoolJsonDeserializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
class HutoolJsonDeserializerTest {
	private final ObjectMapper objectMapper = createObjectMapper();

	@Test
	void shouldDeserializeObjectToken() throws Exception {
		JSON json = objectMapper.readValue("{\"name\":\"tom\",\"count\":1}", JSON.class);

		assertInstanceOf(JSONObject.class, json);
		JSONObject jsonObject = (JSONObject) json;
		assertEquals("tom", jsonObject.getStr("name"));
		assertEquals(1, jsonObject.getInt("count").intValue());
	}

	@Test
	void shouldDeserializeArrayToken() throws Exception {
		JSON json = objectMapper.readValue("[1,{\"name\":\"tom\"}]", JSON.class);

		assertInstanceOf(JSONArray.class, json);
		JSONArray jsonArray = (JSONArray) json;
		assertEquals(2, jsonArray.size());
		assertEquals(1, jsonArray.getInt(0).intValue());
		assertEquals("tom", jsonArray.getJSONObject(1).getStr("name"));
	}

	@Test
	void shouldDeserializeWrappedJsonString() throws Exception {
		JSON json = objectMapper.readValue("\"{\\\"name\\\":\\\"tom\\\",\\\"count\\\":1}\"", JSON.class);

		assertInstanceOf(JSONObject.class, json);
		JSONObject jsonObject = (JSONObject) json;
		assertEquals("tom", jsonObject.getStr("name"));
		assertEquals(1, jsonObject.getInt("count").intValue());
	}

	@Test
	void shouldRejectScalarTokens() {
		assertThrows(MismatchedInputException.class, () -> objectMapper.readValue("\"hello\"", JSON.class));
		assertThrows(MismatchedInputException.class, () -> objectMapper.readValue("1", JSON.class));
		assertThrows(MismatchedInputException.class, () -> objectMapper.readValue("true", JSON.class));
	}

	@Test
	void shouldDeserializeNullValue() throws Exception {
		assertNull(objectMapper.readValue("null", JSON.class));
	}

	private ObjectMapper createObjectMapper() {
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addDeserializer(JSON.class, new HutoolJsonDeserializer());
		return new ObjectMapper().registerModule(simpleModule);
	}
}
