package org.zero.common.data.model.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
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
class SecurityLoginUserTest {
	@Test
	void shouldKeepAttributeReference() {
		Map<String, Object> attributes = new LinkedHashMap<>();
		attributes.put("tenant", "zero");
		SecurityLoginUser loginUser = new SecurityLoginUser(1L, "alice", "password", attributes, authorities());

		attributes.put("tenant", "changed");

		assertAll(
				() -> assertEquals(Long.valueOf(1L), loginUser.getId()),
				() -> assertEquals("alice", loginUser.getName()),
				() -> assertEquals("changed", loginUser.getAttributeOpt("tenant", String.class).orElse(null)),
				() -> assertNotNull(loginUser.getAttributes()),
				() -> assertSame(attributes, loginUser.getAttributes())
		);
	}

	@Test
	void shouldDefaultAttributesToMutableMapWhenNotProvided() {
		SecurityLoginUser loginUser = new SecurityLoginUser(5L, "bob", "password", authorities());

		assertTrue(loginUser.getAttributes().isEmpty());

		loginUser.getAttributes().put("extra", "value");

		assertEquals("value", loginUser.getAttributeOpt("extra", String.class).orElse(null));
	}

	private Collection<? extends GrantedAuthority> authorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	}
}
