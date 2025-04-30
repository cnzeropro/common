package org.zero.common.core.support.bean.dynamic;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.zero.common.core.util.javax.servlet.RequestUtil;

import java.util.Map;

/**
 * {@link DynamicBean} 参数解析器
 * <p>
 * 因为 {@link DynamicBean} 继承了 {@link Map}, 所以在参数解析时，会优先使用 {@linkplain org.springframework.web.method.annotation.MapMethodProcessor MapMethodProcessor} 参数解析器，
 * 因此需要自定义一个注解 {@link DynamicBeanParam} 来标识该参数，避免被 {@linkplain org.springframework.web.method.annotation.MapMethodProcessor MapMethodProcessor} 参数解析器解析
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/21
 */
@RequiredArgsConstructor
public class DynamicBeanArgumentResolver implements HandlerMethodArgumentResolver {
    private final boolean needSplit;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return DynamicBean.class.equals(parameter.getParameterType()) && parameter.hasParameterAnnotation(DynamicBeanParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Map<String, String[]> parameterMap = webRequest.getParameterMap();
        Map<String, Object> simplifiedMap = RequestUtil.simplify(parameterMap, needSplit);
        return DynamicBean.create(simplifiedMap);
    }
}
