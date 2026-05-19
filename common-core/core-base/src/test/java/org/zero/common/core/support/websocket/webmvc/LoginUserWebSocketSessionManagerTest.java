package org.zero.common.core.support.websocket.webmvc;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;
import org.zero.common.data.model.security.SecurityLoginUser;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class LoginUserWebSocketSessionManagerTest {
	@Test
	void shouldManageSessionsByLoginUserId() {
		LoginUserWebSocketSessionManager sessionManager = new LoginUserWebSocketSessionManager();
		Map<String, Object> attributes = new HashMap<>();
		attributes.put(
				LoginUserHandshakeInterceptor.LOGIN_USER_KEY,
				new SecurityLoginUser(5L, "socket-user", "password", Collections.emptyList())
		);
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
