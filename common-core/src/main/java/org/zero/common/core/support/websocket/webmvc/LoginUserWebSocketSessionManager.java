package org.zero.common.core.support.websocket.webmvc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;
import org.zero.common.core.support.cache.Cache;
import org.zero.common.core.support.cache.MapCache;
import org.zero.common.data.exception.CommonException;
import org.zero.common.data.model.security.LoginUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/11
 */
@Getter
@Setter
public class LoginUserWebSocketSessionManager extends WebSocketSessionManager {
    protected Cache<Serializable, Collection<WebSocketSession>> userSessionCache = MapCache.of(ConcurrentHashMap::new);

    @Override
    public void add(WebSocketSession session) {
        Serializable userId = this.getUserId(session);
        userSessionCache.mapAndPut(userId, (id, sessions) -> {
            if (sessions == null) {
                sessions = new ArrayList<>();
            }
            sessions.add(session);
            return sessions;
        });
        super.add(session);
    }

    public Collection<WebSocketSession> getSessions(Serializable userId) {
        return Optional.ofNullable(userId)
                .map(id -> userSessionCache.get(id))
                .map(Collections::unmodifiableCollection)
                .orElseGet(Collections::emptyList);
    }

    @Override
    public void delete(WebSocketSession session) {
        Serializable userId = this.getUserId(session);
        userSessionCache.mapAndPut(userId, (id, sessions) -> {
            sessions.remove(session);
            return sessions;
        });
        super.delete(session);
    }

    protected Serializable getUserId(WebSocketSession session) {
        return Optional.ofNullable(session)
                .map(WebSocketSession::getAttributes)
                .map(map -> map.get(LoginUserHandshakeInterceptor.LOGIN_USER_KEY))
                .filter(LoginUser.class::isInstance)
                .map(LoginUser.class::cast)
                .map(LoginUser::getId)
                .orElseThrow(() -> new CommonException("Failed to obtain logged-in user information"));
    }
}
