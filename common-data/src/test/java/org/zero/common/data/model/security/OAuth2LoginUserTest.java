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

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class OAuth2LoginUserTest {
	@Test
	void shouldUseConfiguredNameAttributeSnapshot() {
		Map<String, Object> attributes = new LinkedHashMap<>();
		attributes.put("login", "octocat");
		OAuth2LoginUser loginUser = new OAuth2LoginUser(3L, authorities(), attributes, "login");

		attributes.put("login", "changed");

		assertAll(
				() -> assertEquals(Long.valueOf(3L), loginUser.getId()),
				() -> assertEquals("octocat", loginUser.getName()),
				() -> assertEquals("octocat", loginUser.getAttributeOpt("login", String.class).orElse(null)),
				() -> assertNotNull(loginUser.getAttributes())
		);
	}

	private Collection<? extends GrantedAuthority> authorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	}
}
