package org.zero.common.core.exception.controller;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Spring Boot 全局错误处理控制器，负责生成统一错误响应
 * <p>
 * 当其他异常处理机制未处理异常时，作为兜底机制触发
 *
 * @author zero
 * @see org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController
 * @see org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration#basicErrorController(ErrorAttributes, ObjectProvider)
 * @since 2024/4/12
 */
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class CustomErrorController extends BasicErrorController {
    public CustomErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties) {
        super(errorAttributes, errorProperties);
    }

    public CustomErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorProperties, errorViewResolvers);
    }

    /**
     * "timestamp": "2025-03-06 16:52:35.487"<br>
     * "status": 500<br>
     * "error": "Internal Server Error"<br>
     * "trace": "org.zero.common.data.exception.CommonException: xxx\r\n\tat org.zero.common.core.support.export.FileExportResponseBodyAdvice.handleFile(FileExportResponseBodyAdvice.java:139)\r\n\t"<br>
     * "message": "xxx"<br>
     * "path": "/export/e1"
     */
    @ResponseBody
    @RequestMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> errorText(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity<>(status);
        }
        Map<String, Object> body = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.TEXT_PLAIN));
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        body.forEach((key, value) -> joiner.add(key + ": " + value));
        return new ResponseEntity<>(joiner.toString(), status);
    }

    /**
     * {
     * "timestamp": "2025-03-06 16:52:35.487",
     * "status": 500,
     * "error": "Internal Server Error",
     * "trace": "org.zero.common.data.exception.CommonException: xxx\r\n\tat org.zero.common.core.support.export.FileExportResponseBodyAdvice.handleFile(FileExportResponseBodyAdvice.java:139)\r\n\t",
     * "message": "xxx",
     * "path": "/export/e1"
     * }
     */
    @ResponseBody
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> errorJson(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity<>(status);
        }
        Map<String, Object> body = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.APPLICATION_JSON));
        return new ResponseEntity<>(body, status);
    }

    /**
     * 需要 {@code jackson-dataformat-xml} lib 支持
     * <p>
     * &lt;Result&gt;<br>
     * &lt;Timestamp&gt;2025-03-06 17:51:15.245&lt;/Timestamp&gt;<br>
     * &lt;Status&gt;500&lt;/Status&gt;<br>
     * &lt;Error&gt;Internal Server Error&lt;/Error&gt;<br>
     * &lt;Trace&gt;org.zero.common.data.exception.CommonException: xxx&#xd;
     * at org.zero.common.core.support.export.FileExportResponseBodyAdvice.handleFile(FileExportResponseBodyAdvice.java:139)&#xd;
     * &lt;/Trace&gt;
     * &lt;Message&gt;xxx&lt;/Message&gt;<br>
     * &lt;Path&gt;/export/e1&lt;/Path&gt;<br>
     * &lt;/Result&gt;
     */
    @ResponseBody
    @RequestMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<SpringXmlResult> errorXml(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return ResponseEntity.status(status).build();
        }
        Map<String, Object> body = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.APPLICATION_XML));
        SpringXmlResult result = SpringXmlResult.of(body);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(result);
    }
}
