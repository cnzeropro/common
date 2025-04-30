package org.zero.common.core.support.websocket.webmvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.zero.common.core.extension.java.lang.reflect.TypeReference;
import org.zero.common.core.util.jackson.databind.JacksonUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/14
 */
@Slf4j
public class JsonWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketMessageListener<Object>> listeners = new HashMap<>();

    public JsonWebSocketHandler(WebSocketMessageListener<Object>... listeners) {
        for (WebSocketMessageListener<Object> listener : listeners) {
            this.listeners.put(listener.getType(), listener);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if (message.getPayloadLength() <= 0) {
            return;
        }
        String payload = message.getPayload();
        JsonMessage jsonMessage = JacksonUtils.toObj(payload, JsonMessage.class);
        WebSocketMessageListener<Object> messageListener = listeners.get(jsonMessage.getType());
        if (Objects.isNull(messageListener)) {
            log.warn("No message listener found: {}", jsonMessage.getType());
            return;
        }
        Type type = TypeReference.getTypeArgument(messageListener.getClass(), 0);
        Object messageObj = JacksonUtils.toObj(jsonMessage.getContent(), type);
        messageListener.onMessage(session, messageObj);
    }
}
