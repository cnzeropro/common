package org.zero.common.test.feature.api.definition;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.HttpRequestHandlerServlet;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/23
 */
@Configuration(proxyBeanMethods = false)
class TestConfig implements WebMvcConfigurer {

    /**
     * 注册静态资源以定义接口
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    }

    /**
     * 注册视图控制器以定义接口
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
    }

    static final String HTTP_REQUEST_HANDLER_SERVLET_NAME = "httpRequestHandler";

    @Bean
    ServletRegistrationBean<HttpRequestHandlerServlet> servletRegistrationBean() {
        ServletRegistrationBean<HttpRequestHandlerServlet> registrar = new ServletRegistrationBean<>();
        registrar.setServlet(new HttpRequestHandlerServlet());
        // 注意：设置的 servlet 名称必须和 HttpRequestHandler 注入到容器的 bean 名称一致
        registrar.setName(HTTP_REQUEST_HANDLER_SERVLET_NAME);
        registrar.addUrlMappings("/api/handler");
        return registrar;
    }

    @Bean
    SimpleServletHandlerAdapter simpleServletHandlerAdapter() {
        return new SimpleServletHandlerAdapter();
    }

    @Bean
    RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route()
                .GET("/api/handler-function", new ImplementHandlerFunction())
                .build();
    }
}
