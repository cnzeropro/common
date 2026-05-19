package org.zero.common.core.support.pingpong;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/07/15
 */
class SocketPingClientTest {
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
}
