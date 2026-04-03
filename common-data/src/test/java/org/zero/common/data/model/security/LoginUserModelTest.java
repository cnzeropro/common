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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
class LoginUserModelTest {
	@Test
	void shouldKeepSecurityLoginUserAttributeReference() {
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
	void shouldKeepShiroLoginUserAttributeReference() {
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
	void shouldUseOAuth2NameAttribute() {
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

	@Test
	void shouldUseOidcSubjectName() {
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

	@Test
	void shouldDefaultAttributesToMutableMapWhenNotProvided() {
		SecurityLoginUser securityLoginUser = new SecurityLoginUser(5L, "bob", "password", authorities());
		ShiroLoginUser shiroLoginUser = new ShiroLoginUser(6L, "readonly");

		assertAll(
			() -> assertTrue(securityLoginUser.getAttributes().isEmpty()),
			() -> assertTrue(shiroLoginUser.getAttributes().isEmpty())
		);

		securityLoginUser.getAttributes().put("extra", "value");
		shiroLoginUser.getAttributes().put("extra", "value");

		assertAll(
			() -> assertEquals("value", securityLoginUser.getAttributeOpt("extra", String.class).orElse(null)),
			() -> assertEquals("value", shiroLoginUser.getAttributeOpt("extra", String.class).orElse(null))
		);
	}

	private Collection<? extends GrantedAuthority> authorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	}
}
