package org.zero.common.core.support.common;

import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.zero.common.core.util.java.reflect.ClassUtil;
import org.zero.common.data.model.view.Result;

/**
 * 统一返回结果处理
 * <p>
 * 使用方式：
 * <p>
 * 方式一：添加到包扫描路径
 * <pre>{@code
 *     @SpringBootApplication(scanBasePackages = {"org.zero.common.core.support.common"})
 * }</pre>
 * 或
 * <pre>{@code
 *     @ComponentScan(scanBasePackages = {"org.zero.common.core.support.common"})
 * }</pre>
 * 方式二：使用 @Bean 注入（推荐）
 * <pre>{@code
 *     @Configuration(proxyBeanMethods = false)
 *     public class AppConfig {
 *         @Bean
 *         ResultResponseBodyAdvice resultResponseBodyAdvice() {
 *             return new ResultResponseBodyAdvice("aaa.bbb", "xxx.yyy.zzz");
 *         }
 *     }
 * }</pre>
 * 方式三：使用 @Import 注入
 * <pre>{@code
 *     @Import({ResultResponseBodyAdvice.class})
 *     @Configuration(proxyBeanMethods = false)
 *     public class AppConfig {
 *     }
 * }</pre>
 *
 * @author Zero (cnzeropro@163.com)
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyAdviceChain
 * @since 2024/8/26
 */
@RestControllerAdvice
public class ResultResponseBodyAdvice implements ResponseBodyAdvice<Object>, Ordered {
    /**
     * 跳过的包名前缀
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
        // 跳过指定注解
        return !returnType.hasMethodAnnotation(IgnorePackResult.class) || !containingClass.isAnnotationPresent(IgnorePackResult.class);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (body instanceof Result ||
                // ResponseEntity 不做处理
                body instanceof ResponseEntity) {
            return body;
        }

        return Result.ok(body);
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
