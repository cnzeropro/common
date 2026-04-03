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
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.web.socket.WebSocketSession;
import org.zero.common.core.support.websocket.webmvc.LoginUserHandshakeInterceptor;
import org.zero.common.core.support.websocket.webmvc.LoginUserWebSocketSessionManager;
import org.zero.common.core.util.apache.shiro.ShiroUtil;
import org.zero.common.core.util.spring.security.core.context.SecurityUtil;
import org.zero.common.data.model.security.LoginUser;
import org.zero.common.data.model.security.OAuth2LoginUser;
import org.zero.common.data.model.security.OidcLoginUser;
import org.zero.common.data.model.security.SecurityLoginUser;
import org.zero.common.data.model.security.ShiroLoginUser;

import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
class LoginUserUtilTest {
	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
		ThreadContext.remove();
	}

	@Test
	void shouldResolveSecurityLoginUserFromSpringSecurityContext() {
		SecurityLoginUser loginUser = new SecurityLoginUser(
			1L,
			"alice",
			"password",
			Collections.singletonMap("tenant", "zero"),
			authorities()
		);

		bindAuthentication(loginUser);

		assertAll(
			() -> assertSame(loginUser, SecurityUtil.getUser()),
			() -> assertSame(loginUser, LoginUserUtil.get()),
			() -> assertEquals(Long.valueOf(1L), SecurityUtil.getUserId()),
			() -> assertEquals("alice", SecurityUtil.getUsername())
		);
	}

	@Test
	void shouldResolveOAuth2LoginUserFromSpringSecurityContext() {
		OAuth2LoginUser loginUser = new OAuth2LoginUser(
			2L,
			authorities(),
			Collections.<String, Object>singletonMap("login", "octocat"),
			"login"
		);

		bindAuthentication(loginUser);

		assertAll(
			() -> assertSame(loginUser, SecurityUtil.getUser()),
			() -> assertSame(loginUser, LoginUserUtil.get()),
			() -> assertEquals(Long.valueOf(2L), LoginUserUtil.getId()),
			() -> assertEquals("octocat", LoginUserUtil.getName())
		);
	}

	@Test
	void shouldResolveOidcLoginUserFromSpringSecurityContext() {
		Map<String, Object> claims = new LinkedHashMap<>();
		claims.put("sub", "oidc-user");
		claims.put("email", "oidc@example.com");
		OidcIdToken idToken = new OidcIdToken(
			"token-value",
			Instant.parse("2026-04-02T00:00:00Z"),
			Instant.parse("2026-04-02T01:00:00Z"),
			claims
		);
		OidcLoginUser loginUser = new OidcLoginUser(3L, authorities(), idToken);

		bindAuthentication(loginUser);

		assertAll(
			() -> assertSame(loginUser, SecurityUtil.getUser()),
			() -> assertSame(loginUser, LoginUserUtil.get()),
			() -> assertEquals(Long.valueOf(3L), LoginUserUtil.getId()),
			() -> assertEquals("oidc-user", LoginUserUtil.getName())
		);
	}

	@Test
	void shouldFallbackToShiroLoginUserWhenSpringSecurityIsAbsent() {
		ShiroLoginUser loginUser = new ShiroLoginUser(4L, "shiro-user", Collections.singletonMap("dept", "platform"));

		bindShiroSubject(loginUser);

		assertAll(
			() -> assertSame(loginUser, ShiroUtil.getUser()),
			() -> assertSame(loginUser, LoginUserUtil.get()),
			() -> assertEquals(Long.valueOf(4L), LoginUserUtil.getId()),
			() -> assertEquals("shiro-user", ShiroUtil.getUsername())
		);
	}

	@Test
	void shouldStoreLoginUserDuringHandshakeAndManageSessionsByUserId() throws Exception {
		SecurityLoginUser loginUser = new SecurityLoginUser(
			5L,
			"socket-user",
			"password",
			Collections.singletonMap("channel", "notify"),
			authorities()
		);
		LoginUserHandshakeInterceptor interceptor = new LoginUserHandshakeInterceptor();
		Map<String, Object> attributes = new HashMap<>();

		bindAuthentication(loginUser);

		assertTrue(interceptor.beforeHandshake(null, null, null, attributes));
		assertSame(loginUser, attributes.get(LoginUserHandshakeInterceptor.LOGIN_USER_KEY));

		LoginUserWebSocketSessionManager sessionManager = new LoginUserWebSocketSessionManager();
		WebSocketSession session = createSession("session-1", attributes);

		sessionManager.add(session);

		assertAll(
			() -> assertEquals(1, sessionManager.getSessions(5L).size()),
			() -> assertSame(session, sessionManager.getSessions(5L).iterator().next()),
			() -> assertThrows(UnsupportedOperationException.class, () -> sessionManager.getSessions(5L).add(session))
		);

		sessionManager.delete(session);

		assertTrue(sessionManager.getSessions(5L).isEmpty());
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

	private WebSocketSession createSession(String id, Map<String, Object> attributes) {
		return (WebSocketSession) Proxy.newProxyInstance(
			WebSocketSession.class.getClassLoader(),
			new Class<?>[]{WebSocketSession.class},
			(proxy, method, args) -> {
				String methodName = method.getName();
				if ("getId".equals(methodName)) {
					return id;
				}
				if ("getAttributes".equals(methodName)) {
					return attributes;
				}
				if ("equals".equals(methodName)) {
					return proxy == args[0];
				}
				if ("hashCode".equals(methodName)) {
					return System.identityHashCode(proxy);
				}
				if ("toString".equals(methodName)) {
					return "TestWebSocketSession[" + id + "]";
				}
				throw new UnsupportedOperationException("Unexpected method: " + methodName);
			}
		);
	}
}
