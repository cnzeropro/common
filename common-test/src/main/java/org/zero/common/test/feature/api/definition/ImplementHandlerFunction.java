package org.zero.common.test.feature.api.definition;

import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * 实现 {@link HandlerFunction} 定义接口
 *
 * @author Zero (cnzeropro@163.com)
 * @see TestConfig#routerFunction()
 * @since 2025/5/23
 */
class ImplementHandlerFunction implements HandlerFunction<ServerResponse> {
    @Override
    public ServerResponse handle(ServerRequest request) throws Exception {
        return ServerResponse.ok().body(this.getClass().getName());
    }
}
