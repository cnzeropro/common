package org.zero.common.test.feature.api.definition;

import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 实现 {@link HttpRequestHandler} 定义接口
 * <p>
 * 注意：注册到容器的 bean 名称为接口地址
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/23
 */
@Component("/api/http-request-handler")
class ImplementHttpRequestHandler implements HttpRequestHandler {
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (PrintWriter writer = response.getWriter()) {
            writer.println("hello world");
            writer.flush();
        }
    }
}
