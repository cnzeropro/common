package org.zero.common.data.enumeration;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/31
 */
class HttpStatusTest {

	@Test
	void resolveShouldPreferLatestRegisteredNameForDuplicateStatusCodes() {
		assertSame(HttpStatus.EARLY_HINTS, HttpStatus.resolve(103));
		assertSame(HttpStatus.CONTENT_TOO_LARGE, HttpStatus.resolve(413));
		assertSame(HttpStatus.RANGE_NOT_SATISFIABLE, HttpStatus.resolve(416));
		assertSame(HttpStatus.UNUSED_418, HttpStatus.resolve(418));
		assertSame(HttpStatus.MISDIRECTED_REQUEST, HttpStatus.resolve(421));
		assertSame(HttpStatus.UNPROCESSABLE_CONTENT, HttpStatus.resolve(422));
	}

	@Test
	void obsoleteAndUnusedStatusesShouldRemainResolvable() {
		assertSame(HttpStatus.UNUSED_306, HttpStatus.resolve(306));
		assertEquals("(Unused)", HttpStatus.UNUSED_306.reasonPhrase());
		assertSame(HttpStatus.NOT_EXTENDED, HttpStatus.resolve(510));
		assertEquals("Not Extended (OBSOLETED)", HttpStatus.NOT_EXTENDED.reasonPhrase());
		assertSame(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED, HttpStatus.resolve(511));
	}

	@Test
	void deprecatedEnumsShouldKeepDeprecatedAnnotations() throws NoSuchFieldException {
		assertDeprecated(HttpStatus.class, "PROCESSING");
		assertDeprecated(HttpStatus.class, "CHECKPOINT");
		assertDeprecated(HttpStatus.class, "MOVED_TEMPORARILY");
		assertDeprecated(HttpStatus.class, "USE_PROXY");
		assertDeprecated(HttpStatus.class, "UNUSED_306");
		assertDeprecated(HttpStatus.class, "PAYLOAD_TOO_LARGE");
		assertDeprecated(HttpStatus.class, "REQUEST_ENTITY_TOO_LARGE");
		assertDeprecated(HttpStatus.class, "REQUEST_URI_TOO_LONG");
		assertDeprecated(HttpStatus.class, "REQUESTED_RANGE_NOT_SATISFIABLE");
		assertDeprecated(HttpStatus.class, "UNUSED_418");
		assertDeprecated(HttpStatus.class, "I_AM_A_TEAPOT");
		assertDeprecated(HttpStatus.class, "INSUFFICIENT_SPACE_ON_RESOURCE");
		assertDeprecated(HttpStatus.class, "METHOD_FAILURE");
		assertDeprecated(HttpStatus.class, "DESTINATION_LOCKED");
		assertDeprecated(HttpStatus.class, "UNPROCESSABLE_ENTITY");
		assertDeprecated(HttpStatus.class, "BANDWIDTH_LIMIT_EXCEEDED");
		assertDeprecated(HttpStatus.class, "NOT_EXTENDED");
	}

	private static void assertDeprecated(Class<?> owner, String fieldName) throws NoSuchFieldException {
		Field field = owner.getField(fieldName);
		Deprecated deprecated = field.getAnnotation(Deprecated.class);
		assertNotNull(deprecated, "Expected @Deprecated on " + owner.getSimpleName() + "." + fieldName);
	}
}
