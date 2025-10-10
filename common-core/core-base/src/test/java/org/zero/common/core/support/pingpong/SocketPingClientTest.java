package org.zero.common.core.support.pingpong;

import org.junit.jupiter.api.Test;

import java.util.Scanner;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/15
 */
class SocketPingClientTest {
    @Test
    void serverTest() throws Exception {
        SocketPongServer pongServer = SocketPongServer.builder()
                .host("127.0.0.1")
                .port(8080)
                .build();
        pongServer.initialize();
        pongServer.start();
        // 阻塞
        new Scanner(System.in).nextLine();
        pongServer.close();
    }

    @Test
    void clientTest() throws Exception {
        SocketPingClient pingClient = SocketPingClient.builder()
                .serverHost("127.0.0.1")
                .serverPort(8080)
                .period(3000)
                .build();
        pingClient.initialize();
        pingClient.start();
        // 阻塞
        new Scanner(System.in).nextLine();
        pingClient.close();
    }
}