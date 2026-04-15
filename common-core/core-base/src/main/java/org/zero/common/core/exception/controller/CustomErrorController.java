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
 * Spring Boot 全局错误控制器，负责在兜底错误分发阶段输出统一格式的错误响应。
 * <p>
 * 当常规异常处理器未消费异常，或请求直接进入 {@code /error} 分发链路时，
 * 根据协商的媒体类型返回纯文本、JSON 或 XML 响应。
 *
 * @author Zero (cnzeropro@163.com)
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
	 * 返回纯文本错误响应。
	 * <p>
	 * 每行输出一个错误属性，格式为 {@code key: value}，便于命令行或文件下载等场景直接查看。
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
	 * 返回 JSON 错误响应。
	 * <p>
	 * 响应体直接透传 Spring Boot 生成的错误属性映射，HTTP 状态码与底层错误状态保持一致。
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
	 * 返回 XML 错误响应。
	 * <p>
	 * 需要 {@code jackson-dataformat-xml} 支持，并将 Spring Boot 生成的错误属性转换为 {@link SpringXmlResult}。
	 * HTTP 状态码与底层错误状态保持一致。
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
		return ResponseEntity.status(status)
			.contentType(MediaType.APPLICATION_XML)
			.body(result);
	}
}
