// package org.zero.common.test.feature.api.definition;
//
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.service.annotation.GetExchange;
// import org.springframework.web.service.annotation.HttpExchange;
//
// /**
//  * 使用 Spring Boot 3 提供的 {@linkplain HttpExchange @HttpExchange} 定义接口
//  * <p>
//  * 尽管 {@linkplain HttpExchange @HttpExchange} 的核心用途是通过生成的代理对象来抽象化 HTTP 客户端代码，
//  * 但该注解的 HTTP 接口本身是一个不区分客户端与服务器端使用场景的契约规范。
//  *
//  * @author Zero (cnzeropro@163.com)
//  * @since 2025/5/23
//  */
// @HttpExchange("api")
// interface HttpExchangeAnnotation {
//     @GetExchange("http-exchange-annotation")
//     ResponseEntity<String> query();
// }
