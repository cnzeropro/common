package org.zero.common.data.enumeration;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/3
 */
class MimeTypeTest {

	@Test
	void constructorShouldDefaultSubtypeAndParameters() {
		MimeType mimeType = new MimeType("text");

		assertAll(
			() -> assertEquals("text", mimeType.getType()),
			() -> assertEquals("*", mimeType.getSubtype()),
			() -> assertTrue(mimeType.getParameters().isEmpty()),
			() -> assertEquals("text/*", mimeType.toString())
		);
	}

	@Test
	void toStringShouldAppendParametersInIterationOrder() {
		Map<String, String> parameters = new LinkedHashMap<>();
		parameters.put("charset", "UTF-8");
		parameters.put("version", "1");
		MimeType mimeType = new MimeType("application", "json", parameters);

		assertAll(
			() -> assertEquals("UTF-8", mimeType.getParameter("charset")),
			() -> assertNull(mimeType.getParameter("missing")),
			() -> assertEquals("application/json;charset=UTF-8;version=1", mimeType.toString())
		);
	}
}
