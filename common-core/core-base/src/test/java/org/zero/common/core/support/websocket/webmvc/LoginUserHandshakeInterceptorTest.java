package org.zero.common.core.support.websocket.webmvc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zero.common.data.model.security.LoginUser;
import org.zero.common.data.model.security.SecurityLoginUser;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class LoginUserHandshakeInterceptorTest {
	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void shouldStoreLoginUserDuringHandshake() throws Exception {
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
	}

	private void bindAuthentication(LoginUser loginUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(new TestingAuthenticationToken(loginUser, null, "ROLE_USER"));
		SecurityContextHolder.setContext(context);
	}

	private Collection<? extends GrantedAuthority> authorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
	}
}
