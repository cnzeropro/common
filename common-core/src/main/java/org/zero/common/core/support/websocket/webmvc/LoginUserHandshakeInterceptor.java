package org.zero.common.core.support.websocket.webmvc;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.zero.common.core.util.LoginUserUtil;
import org.zero.common.data.model.security.LoginUser;

import java.util.Map;
import java.util.Optional;

/**
 * 登录用户握手拦截器
 * <p>
 * 需要和鉴权体系配合使用
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/14
 */
public class LoginUserHandshakeInterceptor implements HandshakeInterceptor {
    public static final String LOGIN_USER_KEY = "loginUser";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        Optional<LoginUser> loginUserOpt = LoginUserUtil.getOpt();
        if (loginUserOpt.isPresent()) {
            attributes.put(LOGIN_USER_KEY, loginUserOpt.get());
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // do nothing
    }
}
