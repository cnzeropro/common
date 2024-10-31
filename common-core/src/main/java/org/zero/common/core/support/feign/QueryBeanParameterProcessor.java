package org.zero.common.core.support.feign;

import feign.MethodMetadata;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 注入到 Spring 容器使用
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/31
 */
@Component
public class QueryBeanParameterProcessor implements AnnotatedParameterProcessor {
    private static final Class<QueryBean> ANNOTATION = QueryBean.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context, Annotation annotation, Method method) {
        MethodMetadata data = context.getMethodMetadata();
        // 设置 alwaysEncodeBody = true，详情参见 feign.ReflectiveFeign.ParseHandlersByName.apply 方法
        // 因为 feign.MethodMetadata.alwaysEncodeBody(boolean) 方法目前是默认（default）的，所以只能通过反射调用
        // data.alwaysEncodeBody(true);
        Method alwaysEncodeBodyMethod = ReflectionUtils.findMethod(data.getClass(), "alwaysEncodeBody", boolean.class);
        Assert.notNull(alwaysEncodeBodyMethod, "alwaysEncodeBody method not found");
        ReflectionUtils.makeAccessible(alwaysEncodeBodyMethod);
        ReflectionUtils.invokeMethod(alwaysEncodeBodyMethod, data, true);
        // 必须返回 true，详情参见 feign.Contract.BaseContract.parseAndValidateMetadata(java.lang.Class<?>, java.lang.reflect.Method) 方法
        return true;
    }
}
