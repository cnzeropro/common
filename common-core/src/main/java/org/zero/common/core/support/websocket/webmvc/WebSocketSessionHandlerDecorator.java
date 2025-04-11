package org.zero.common.core.support.websocket.webmvc;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.zero.common.core.support.websocket.WebSocketSessionManager;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/11
 */
public class WebSocketSessionHandlerDecorator extends WebSocketHandlerDecorator {
    private final WebSocketSessionManager<WebSocketSession> sessionManager;
    /**
     * 发送时间限制，单位：ms
     */
    private int sendTimeLimit = 5 * 1000;
    /**
     * 缓冲区大小限制，单位：bytes
     */
    private int bufferSizeLimit = 1024 * 1024;
    /**
     * 缓冲区填满时要执行的策略
     */
    private ConcurrentWebSocketSessionDecorator.OverflowStrategy overflowStrategy = ConcurrentWebSocketSessionDecorator.OverflowStrategy.TERMINATE;


    public WebSocketSessionHandlerDecorator(WebSocketHandler delegate,
                                            WebSocketSessionManager<WebSocketSession> sessionManager) {
        super(delegate);
        this.sessionManager = sessionManager;
    }

    public WebSocketSessionHandlerDecorator(WebSocketHandler delegate,
                                            WebSocketSessionManager<WebSocketSession> sessionManager,
                                            int sendTimeLimit,
                                            int bufferSizeLimit,
                                            ConcurrentWebSocketSessionDecorator.OverflowStrategy overflowStrategy) {
        super(delegate);
        this.sessionManager = sessionManager;
        this.sendTimeLimit = sendTimeLimit;
        this.bufferSizeLimit = bufferSizeLimit;
        this.overflowStrategy = overflowStrategy;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // session 支持并发，参考 https://blog.csdn.net/abu935009066/article/details/131218149
        session = new ConcurrentWebSocketSessionDecorator(session, sendTimeLimit, bufferSizeLimit, overflowStrategy);
        // 添加到 SessionManager 中
        sessionManager.set(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        sessionManager.remove(session.getId());
    }
}

