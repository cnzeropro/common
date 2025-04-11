package org.zero.common.core.support.websocket.webflux;

import lombok.experimental.Delegate;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.zero.common.core.support.cache.Cache;
import org.zero.common.core.support.cache.MapCache;
import org.zero.common.core.support.websocket.WebSocketSessionManager;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/11
 */
public class WebSocketSessionManagerImpl implements WebSocketSessionManager<WebSocketSession> {
    @Delegate
    protected Cache<CharSequence, WebSocketSession> cache = MapCache.of(ConcurrentReferenceHashMap::new);
}
