package org.zero.common.core.support.websocket;

import org.zero.common.core.support.cache.Cache;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/11
 */
public interface BaseWebSocketSessionManager<S> extends Cache<Serializable, S> {
    void add(S session);
    void delete(S session);
}
