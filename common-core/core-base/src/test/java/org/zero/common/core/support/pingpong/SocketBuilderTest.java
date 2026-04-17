package org.zero.common.core.support.pingpong;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
class SocketBuilderTest {
	@Test
	void shouldBuildSocketPingClient() {
		SocketPingClient pingClient = SocketPingClient.builder()
				.serverHost("127.0.0.1")
				.serverPort(18080)
				.localHost("127.0.0.1")
				.localPort(18081)
				.timeout(200)
				.period(3_000L)
				.timeUnit(TimeUnit.SECONDS)
				.build();

		assertEquals("127.0.0.1", pingClient.serverAddress.getHostAddress());
		assertEquals(18080, pingClient.serverPort);
		assertEquals("127.0.0.1", pingClient.localAddress.getHostAddress());
		assertEquals(18081, pingClient.localPort);
		assertEquals(200, pingClient.timeout);
		assertEquals(3_000L, pingClient.period);
		assertEquals(TimeUnit.SECONDS, pingClient.timeUnit);
	}

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
