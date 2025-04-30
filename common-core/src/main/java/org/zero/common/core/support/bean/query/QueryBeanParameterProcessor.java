package org.zero.common.core.support.bean.query;

import feign.MethodMetadata;
import feign.RequestTemplate;
import feign.Target;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * {@link QueryBean} parameter processor.
 *
 * @author Zero (cnzeropro@163.com)
 * @see org.springframework.cloud.openfeign.annotation.RequestParamParameterProcessor
 * @since 2024/10/31
 */
public class QueryBeanParameterProcessor implements AnnotatedParameterProcessor {
    private static final Class<QueryBean> ANNOTATION = QueryBean.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    /**
     * @see feign.ReflectiveFeign.BuildEncodedTemplateFromArgs#resolve(Object[], RequestTemplate, Map)
     * @see feign.ReflectiveFeign.ParseHandlersByName#apply(Target)
     * @see feign.Contract.BaseContract#parseAndValidateMetadata(Class, Method)
     */
    @Override
    public boolean processArgument(AnnotatedParameterContext context, Annotation annotation, Method method) {
        MethodMetadata data = context.getMethodMetadata();
        // 设置 alwaysEncodeBody = true
        // 详情参见 feign.ReflectiveFeign.ParseHandlersByName.apply 方法
        // data.alwaysEncodeBody(true);
        // 因为 feign.MethodMetadata.alwaysEncodeBody(boolean) 方法目前是包私有（package-private）的，所以只能通过反射调用
        Method alwaysEncodeBodyMethod = ReflectionUtils.findMethod(data.getClass(), "alwaysEncodeBody", boolean.class);
        Assert.notNull(alwaysEncodeBodyMethod, "MethodMetadata.alwaysEncodeBody method not found");
        ReflectionUtils.makeAccessible(alwaysEncodeBodyMethod);
        ReflectionUtils.invokeMethod(alwaysEncodeBodyMethod, data, true);
        // 必须返回 true，详情参见 feign.Contract.BaseContract#parseAndValidateMetadata(java.lang.Class<?>, java.lang.reflect.Method) 方法
        return true;
    }
}
