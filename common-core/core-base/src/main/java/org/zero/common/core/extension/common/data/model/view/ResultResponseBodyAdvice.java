package org.zero.common.core.extension.common.data.model.view;

import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.data.model.view.Result;

/**
 * 统一包装 Jackson JSON 响应为 {@link Result}。
 * <p>
 * 使用方式：
 * <p>
 * 方式一：加入包扫描路径
 * <pre>{@code
 *     @SpringBootApplication(scanBasePackages = {"org.zero.common.core.extension.common"})
 * }</pre>
 * 或
 * <pre>{@code
 *     @ComponentScan(scanBasePackages = {"org.zero.common.core.extension.common"})
 * }</pre>
 * 方式二：使用 {@code @Bean} 注入（推荐）
 * <pre>{@code
 *     @Configuration(proxyBeanMethods = false)
 *     public class AppConfig {
 *         @Bean
 *         ResultResponseBodyAdvice resultResponseBodyAdvice() {
 *             return new ResultResponseBodyAdvice("aaa.bbb", "xxx.yyy.zzz");
 *         }
 *     }
 * }</pre>
 * 方式三：使用 {@code @Import} 注入
 * <pre>{@code
 *     @Import(ResultResponseBodyAdvice.class)
 *     @Configuration(proxyBeanMethods = false)
 *     public class AppConfig {
 *     }
 * }</pre>
 * <p>
 * 标注 {@link SkipResultWrapping} 的类或方法会跳过包装。
 *
 * @author Zero (cnzeropro@163.com)
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyAdviceChain
 * @since 2024/8/26
 */
@RestControllerAdvice
public class ResultResponseBodyAdvice implements ResponseBodyAdvice<Object>, Ordered {
    /**
	 * 需要跳过包装的包名前缀。
     */
    private String[] skippedPackageNames = {};

    public ResultResponseBodyAdvice() {
    }

    public ResultResponseBodyAdvice(String... skippedPackageNames) {
        this.skippedPackageNames = skippedPackageNames;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> containingClass = returnType.getContainingClass();
        // 跳过指定包名前缀
        if (!ObjectUtils.isEmpty(skippedPackageNames) && ClassUtil.isClassWithPrefix(containingClass, skippedPackageNames)) {
            return false;
        }
		return !this.isSkipResultWrapping(returnType, containingClass);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (body instanceof Result ||
				// HttpEntity 返回类型不做处理
				HttpEntity.class.isAssignableFrom(returnType.getParameterType())) {
			return body;
		}
		// 仅包装 Jackson JSON 响应，避免影响 String、文件下载、流式响应等非 JSON 场景
		if (!MappingJackson2HttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            return body;
        }
        return Result.ok(body);
	}

	private boolean isSkipResultWrapping(MethodParameter returnType, Class<?> containingClass) {
		return returnType.hasMethodAnnotation(SkipResultWrapping.class)
				|| containingClass.isAnnotationPresent(SkipResultWrapping.class);
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
