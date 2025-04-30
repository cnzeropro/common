package org.zero.common.core.support.websocket.webmvc;

import org.springframework.web.socket.WebSocketSession;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/14
 */
public interface WebSocketMessageListener<T> {
    /**
     * 处理消息
     *
     * @param session Session
     * @param message 消息
     */
    void onMessage(WebSocketSession session, T message);

    /**
     * 获得消息类型
     *
     * @return 消息类型
     * @see org.zero.common.core.support.websocket.webmvc.JsonMessage#getType()
     */
    String getType();
}
