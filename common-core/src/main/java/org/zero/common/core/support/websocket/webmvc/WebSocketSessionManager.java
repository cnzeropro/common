package org.zero.common.core.support.websocket.webmvc;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.springframework.web.socket.WebSocketSession;
import org.zero.common.core.support.cache.Cache;
import org.zero.common.core.support.cache.MapCache;
import org.zero.common.core.support.websocket.BaseWebSocketSessionManager;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/11
 */
@Getter
@Setter
public class WebSocketSessionManager implements BaseWebSocketSessionManager<WebSocketSession> {
    @Delegate
    protected Cache<Serializable, WebSocketSession> sessionCache = MapCache.of(ConcurrentHashMap::new);

    @Override
    public void add(WebSocketSession session) {
        this.put(session.getId(), session);
    }

    @Override
    public void delete(WebSocketSession session) {
        this.remove(session.getId());
    }
}
