package org.zero.common.core.util.spring.security.core.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zero.common.data.model.security.SecurityLoginUser;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class SecurityUtilTest {
	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void shouldResolveSecurityLoginUserFromContext() {
		SecurityLoginUser loginUser = new SecurityLoginUser(
				1L,
				"alice",
				"password",
				Collections.singletonMap("tenant", "zero"),
				authorities()
		);

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(new TestingAuthenticationToken(loginUser, null, "ROLE_USER"));
		SecurityContextHolder.setContext(context);

		assertAll(
				() -> assertSame(loginUser, SecurityUtil.getUser()),
				() -> assertEquals(Long.valueOf(1L), SecurityUtil.getUserId()),
				() -> assertEquals("alice", SecurityUtil.getUsername())
		);
	}

	private Collection<? extends GrantedAuthority> authorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	}
}
