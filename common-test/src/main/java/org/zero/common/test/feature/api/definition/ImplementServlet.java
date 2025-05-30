package org.zero.common.test.feature.api.definition;

import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 实现 {@linkplain javax.servlet.Servlet Servlet} 定义接口
 * <p>
 * 注意：
 * 1、注册到容器的 bean 名称为接口地址；
 * 2、默认情况下，此时还不能访问，需要注入 {@linkplain org.springframework.web.servlet.handler.SimpleServletHandlerAdapter SimpleServletHandlerAdapter}
 *
 * @author Zero (cnzeropro@163.com)
 * @see Config#simpleServletHandlerAdapter()
 * @since 2025/5/23
 */
@Component("/api/servlet")
class ImplementServlet extends javax.servlet.http.HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (PrintWriter writer = resp.getWriter()) {
            writer.println("hello world");
            writer.flush();
        }
    }
}
