package org.zero.common.test.feature.api.definition;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 使用 {@link WebServlet @WebServlet} 注解定义接口
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/23
 */
@WebServlet("/api/web-servlet")
class AnnotateWebServlet extends ImplementServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (PrintWriter writer = resp.getWriter()) {
            writer.println("hello world");
            writer.flush();
        }
    }
}
