package org.zero.common.data.model.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;

import java.time.Instant;
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
class OidcLoginUserTest {
	@Test
	void shouldUseSubjectNameFromIdTokenSnapshot() {
		Map<String, Object> claims = new LinkedHashMap<>();
		claims.put("sub", "oidc-user");
		claims.put("email", "oidc@example.com");
		OidcIdToken idToken = new OidcIdToken(
				"token-value",
				Instant.parse("2026-04-02T00:00:00Z"),
				Instant.parse("2026-04-02T01:00:00Z"),
				claims
		);
		OidcLoginUser loginUser = new OidcLoginUser(4L, authorities(), idToken);

		claims.put("sub", "changed");

		assertAll(
				() -> assertEquals(Long.valueOf(4L), loginUser.getId()),
				() -> assertEquals("oidc-user", loginUser.getName()),
				() -> assertEquals("oidc@example.com", loginUser.getAttributeOpt("email", String.class).orElse(null)),
				() -> assertNotNull(loginUser.getAttributes())
		);
	}

	private Collection<? extends GrantedAuthority> authorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	}
}
