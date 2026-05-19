package org.zero.common.data.model.security;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class ShiroLoginUserTest {
	@Test
	void shouldKeepAttributeReference() {
		Map<String, Object> attributes = new LinkedHashMap<>();
		attributes.put("dept", "platform");
		ShiroLoginUser loginUser = new ShiroLoginUser(2L, "shiro-user", attributes);

		attributes.put("dept", "changed");

		assertAll(
				() -> assertEquals(Long.valueOf(2L), loginUser.getId()),
				() -> assertEquals("shiro-user", loginUser.getName()),
				() -> assertEquals("changed", loginUser.getAttributeOpt("dept", String.class).orElse(null)),
				() -> assertNotNull(loginUser.getAttributes()),
				() -> assertSame(attributes, loginUser.getAttributes())
		);
	}

	@Test
	void shouldDefaultAttributesToMutableMapWhenNotProvided() {
		ShiroLoginUser loginUser = new ShiroLoginUser(6L, "readonly");

		assertTrue(loginUser.getAttributes().isEmpty());

		loginUser.getAttributes().put("extra", "value");

		assertEquals("value", loginUser.getAttributeOpt("extra", String.class).orElse(null));
	}
}
