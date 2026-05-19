package org.zero.common.core.util;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zero.common.data.model.security.LoginUser;
import org.zero.common.data.model.security.SecurityLoginUser;
import org.zero.common.data.model.security.ShiroLoginUser;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/04/02
 */
class LoginUserUtilTest {
	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
		ThreadContext.remove();
	}

	@Test
	void shouldResolveSpringSecurityLoginUserFirst() {
		SecurityLoginUser loginUser = new SecurityLoginUser(
			1L,
			"alice",
			"password",
			Collections.singletonMap("tenant", "zero"),
			authorities()
		);

		bindAuthentication(loginUser);

		assertAll(
			() -> assertSame(loginUser, LoginUserUtil.get()),
				() -> assertEquals(Long.valueOf(1L), LoginUserUtil.getId()),
				() -> assertEquals("alice", LoginUserUtil.getName())
		);
	}

	@Test
	void shouldFallbackToShiroLoginUserWhenSpringSecurityIsAbsent() {
		ShiroLoginUser loginUser = new ShiroLoginUser(4L, "shiro-user", Collections.singletonMap("dept", "platform"));

		bindShiroSubject(loginUser);

		assertAll(
			() -> assertSame(loginUser, LoginUserUtil.get()),
			() -> assertEquals(Long.valueOf(4L), LoginUserUtil.getId()),
				() -> assertEquals("shiro-user", LoginUserUtil.getName())
		);
	}

	private void bindAuthentication(LoginUser loginUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(new TestingAuthenticationToken(loginUser, null, "ROLE_USER"));
		SecurityContextHolder.setContext(context);
	}

	private void bindShiroSubject(LoginUser loginUser) {
		DefaultSecurityManager securityManager = new DefaultSecurityManager();
		Subject subject = new Subject.Builder(securityManager)
			.principals(new SimplePrincipalCollection(loginUser, "testRealm"))
			.authenticated(true)
			.buildSubject();
		ThreadContext.bind(securityManager);
		ThreadContext.bind(subject);
	}

	private Collection<? extends GrantedAuthority> authorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	}
}
