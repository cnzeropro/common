package org.zero.common.test.feature.api.definition;

import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.zero.common.test.feature.api.definition.TestConfig.HTTP_REQUEST_HANDLER_SERVLET_NAME;

/**
 * 注册 {@linkplain org.springframework.web.context.support.HttpRequestHandlerServlet HttpRequestHandlerServlet} 定义接口
 * <p>
 * 注册 {@linkplain org.springframework.web.context.support.HttpRequestHandlerServlet HttpRequestHandlerServlet} 方式参见：{@linkplain TestConfig#servletRegistrationBean()}
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/23
 */
@Component(HTTP_REQUEST_HANDLER_SERVLET_NAME)
class ImplementHttpRequestHandlerAndUseHttpRequestHandlerServlet implements HttpRequestHandler {
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (PrintWriter writer = response.getWriter()) {
            writer.println(this.getClass().getName());
            writer.flush();
        }
    }
}
