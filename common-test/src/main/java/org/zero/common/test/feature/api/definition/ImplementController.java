package org.zero.common.test.feature.api.definition;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 实现 {@link Controller} 定义接口
 * <p>
 * 注意：注册到容器的 bean 名称为接口地址
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/23
 */
@Component("/api/controller")
class ImplementController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try (PrintWriter writer = response.getWriter()) {
            writer.println(this.getClass().getName());
            writer.flush();
        }
        return null;
    }
}
