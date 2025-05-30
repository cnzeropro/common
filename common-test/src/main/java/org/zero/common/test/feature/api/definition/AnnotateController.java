package org.zero.common.test.feature.api.definition;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 使用 {@linkplain org.springframework.stereotype.Controller @Controller}/{@linkplain RestController @RestController} 注解定义接口
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/23
 */
@RestController
@RequestMapping("api")
class AnnotateController {
    @GetMapping("annotate-controller")
    ResponseEntity<String> query() {
        return ResponseEntity.ok("hello world");
    }
}
