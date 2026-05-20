package org.zero.common.test.controller;

import cn.hutool.json.JSON;
import org.junit.jupiter.api.Test;
import org.zero.common.data.model.view.Result;

import java.io.Serializable;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/4
 */
class JacksonControllerTest {
	private final JacksonController controller = new JacksonController();

	@Test
	void jsonNullTypeShouldReturnJsonObject() {
		Result<JSON> result = controller.jsonNullType();

		assertTrue(result.isSuccess());
		assertNotNull(result.getData());
	}

	@Test
	void datetimeTypeShouldReturnDatetimeMap() {
		Result<Map<String, Serializable>> result = controller.datetimeType();

		assertTrue(result.isSuccess());
		assertTrue(result.getData().containsKey("localDateTime"));
		assertTrue(result.getData().containsKey("instant"));
	}

	@Test
	void longTypeShouldReturnLongValue() {
		Result<Long> result = controller.longType();

		assertEquals(100L, result.getData());
	}

	@Test
	void doubleTypeShouldReturnDoubleValue() {
		Result<Double> result = controller.doubleType();

		assertEquals(Double.MAX_VALUE, result.getData());
	}
}
