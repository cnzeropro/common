package org.zero.common.core.extension.jackson.databind.ser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
class NumberJsonComponentTest {
	private final ObjectMapper objectMapper = createObjectMapper();

	@Test
	void shouldSerializeIntegerNumbersBySafetyBoundary() throws Exception {
		Map<String, Object> values = new LinkedHashMap<>();
		values.put("safeLong", NumberJsonComponent.LongSerializer.JS_MAX_SAFE_INTEGER);
		values.put("unsafeLong", NumberJsonComponent.LongSerializer.JS_MAX_SAFE_INTEGER + 1L);
		values.put("safeBigInteger", BigInteger.valueOf(NumberJsonComponent.LongSerializer.JS_MAX_SAFE_INTEGER));
		values.put("unsafeBigInteger", BigInteger.valueOf(NumberJsonComponent.LongSerializer.JS_MAX_SAFE_INTEGER).add(BigInteger.ONE));

		JsonNode jsonNode = readTree(values);

		assertTrue(jsonNode.get("safeLong").isNumber());
		assertTrue(jsonNode.get("unsafeLong").isTextual());
		assertEquals(String.valueOf(NumberJsonComponent.LongSerializer.JS_MAX_SAFE_INTEGER + 1L), jsonNode.get("unsafeLong").asText());
		assertTrue(jsonNode.get("safeBigInteger").isNumber());
		assertTrue(jsonNode.get("unsafeBigInteger").isTextual());
	}

	@Test
	void shouldSerializeBigDecimalByPrecisionRisk() throws Exception {
		Map<String, Object> values = new LinkedHashMap<>();
		values.put("safeInteger", new BigDecimal("9007199254740991"));
		values.put("unsafeInteger", new BigDecimal("9007199254740992"));
		values.put("decimalValue", new BigDecimal("1.25"));
		values.put("scientificValue", new BigDecimal("1E+20"));

		String json = objectMapper.writeValueAsString(values);
		JsonNode jsonNode = new ObjectMapper().readTree(json);

		assertTrue(jsonNode.get("safeInteger").isNumber());
		assertTrue(jsonNode.get("unsafeInteger").isTextual());
		assertEquals("9007199254740992", jsonNode.get("unsafeInteger").asText());
		assertTrue(jsonNode.get("decimalValue").isTextual());
		assertEquals("1.25", jsonNode.get("decimalValue").asText());
		assertTrue(jsonNode.get("scientificValue").isTextual());
		assertEquals("100000000000000000000", jsonNode.get("scientificValue").asText());
		assertFalse(json.contains("1E+20"));
	}

	@Test
	void shouldSerializeFloatingNumbersWithoutScientificNotationAndHandleNonFiniteValues() throws Exception {
		Map<String, Object> values = new LinkedHashMap<>();
		values.put("finiteFloat", 1.2E7f);
		values.put("finiteDouble", 1.23E7d);
		values.put("floatNaN", Float.NaN);
		values.put("doubleInfinity", Double.POSITIVE_INFINITY);
		values.put("doubleNegativeInfinity", Double.NEGATIVE_INFINITY);

		String json = objectMapper.writeValueAsString(values);
		JsonNode jsonNode = new ObjectMapper().readTree(json);

		assertTrue(jsonNode.get("finiteFloat").isNumber());
		assertTrue(jsonNode.get("finiteDouble").isNumber());
		assertTrue(json.contains("\"finiteFloat\":12000000"));
		assertTrue(json.contains("\"finiteDouble\":12300000"));
		assertFalse(json.contains("1.2E7"));
		assertFalse(json.contains("1.23E7"));
		assertTrue(jsonNode.get("floatNaN").isTextual());
		assertEquals("NaN", jsonNode.get("floatNaN").asText());
		assertEquals("Infinity", jsonNode.get("doubleInfinity").asText());
		assertEquals("-Infinity", jsonNode.get("doubleNegativeInfinity").asText());
	}

	private JsonNode readTree(Map<String, Object> values) throws Exception {
		return new ObjectMapper().readTree(objectMapper.writeValueAsString(values));
	}

	private ObjectMapper createObjectMapper() {
		JsonComponentModule jsonComponentModule = new JsonComponentModule();
		try (AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext()) {
			applicationContext.register(NumberJsonComponent.class);
			applicationContext.refresh();
			jsonComponentModule.setBeanFactory(applicationContext.getBeanFactory());
			jsonComponentModule.afterPropertiesSet();
		}
		return new ObjectMapper().registerModule(jsonComponentModule);
	}
}
