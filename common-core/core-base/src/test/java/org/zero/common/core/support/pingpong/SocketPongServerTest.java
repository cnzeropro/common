package org.zero.common.core.support.pingpong;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class SocketPongServerTest {
	@Test
	void shouldBuildSocketPongServer() {
		SocketPongServer pongServer = SocketPongServer.builder()
				.host("127.0.0.1")
				.port(18082)
				.timeout(300)
				.backlog(20)
				.reuseAddress(true)
				.build();

		assertEquals("127.0.0.1", pongServer.inetAddress.getHostAddress());
		assertEquals(18082, pongServer.port);
		assertEquals(300, pongServer.timeout);
		assertEquals(20, pongServer.backlog);
		assertTrue(pongServer.reuseAddress);
	}
}
