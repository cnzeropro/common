package org.zero.common.data.constant;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class HttpStatusTest {
	private static void assertDeprecated(String fieldName) throws NoSuchFieldException {
		Field field = HttpStatus.class.getField(fieldName);
		Deprecated deprecated = field.getAnnotation(Deprecated.class);
		assertNotNull(deprecated, "Expected @Deprecated on HttpStatus." + fieldName);
	}

	@Test
	void constantsShouldRetainHistoricalAliasValues() {
		assertEquals(HttpStatus.EARLY_HINTS, HttpStatus.CHECKPOINT);
		assertEquals(HttpStatus.CONTENT_TOO_LARGE, HttpStatus.PAYLOAD_TOO_LARGE);
		assertEquals(HttpStatus.CONTENT_TOO_LARGE, HttpStatus.REQUEST_ENTITY_TOO_LARGE);
		assertEquals(HttpStatus.RANGE_NOT_SATISFIABLE, HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
		assertEquals(HttpStatus.MISDIRECTED_REQUEST, HttpStatus.DESTINATION_LOCKED);
		assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@Test
	void deprecatedConstantsShouldKeepDeprecatedAnnotations() throws NoSuchFieldException {
		assertDeprecated("PROCESSING");
		assertDeprecated("CHECKPOINT");
		assertDeprecated("MOVED_TEMPORARILY");
		assertDeprecated("USE_PROXY");
		assertDeprecated("UNUSED_306");
		assertDeprecated("PAYLOAD_TOO_LARGE");
		assertDeprecated("REQUEST_ENTITY_TOO_LARGE");
		assertDeprecated("REQUEST_URI_TOO_LONG");
		assertDeprecated("REQUESTED_RANGE_NOT_SATISFIABLE");
		assertDeprecated("UNUSED_418");
		assertDeprecated("I_AM_A_TEAPOT");
		assertDeprecated("INSUFFICIENT_SPACE_ON_RESOURCE");
		assertDeprecated("METHOD_FAILURE");
		assertDeprecated("DESTINATION_LOCKED");
		assertDeprecated("UNPROCESSABLE_ENTITY");
		assertDeprecated("BANDWIDTH_LIMIT_EXCEEDED");
		assertDeprecated("NOT_EXTENDED");
	}
}
