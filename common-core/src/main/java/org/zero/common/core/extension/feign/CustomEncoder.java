package org.zero.common.core.extension.feign;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.zero.common.core.util.BeanPathMapUtil;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * 使用方式：<br>
 * 1、注入 {@link QueryBean} 参数处理器（{@link QueryBeanParameterProcessor}）到 Spring 容器<br>
 * 2、为 FeignClient 指定自定义 Encoder（{@link CustomEncoder}）<br>
 * 单独指定
 * <pre>
 * public class AppFeignConfig {
 *     <code>@Bean</code>
 *     public Encoder customEncoder(Encoder encoder) {
 *         return new CustomEncoder(encoder);
 *     }
 * }
 * </pre>
 * 全部指定
 * <pre>
 * <code>@Configuration(proxyBeanMethods = false)</code>
 * <code>@EnableFeignClients</code>
 * public class FeignConfig {
 *     <code>@Bean</code>
 *     public Encoder customEncoder(Encoder encoder) {
 *         return new CustomEncoder(encoder);
 *     }
 * }
 * </pre>
 * 3、在需要的 Feign 方法参数上使用 {@link QueryBean} 注解
 *
 * @author Zero (cnzeropro@163.com)
 * @see QueryBean
 * @see QueryBeanParameterProcessor
 * @since 2024/10/31
 */
public class CustomEncoder implements Encoder {
    protected final Encoder delegate;
    protected String[] beanBasePackages = BeanPathMapUtil.DEFAULT_PACKAGE_LEVEL_NAMES;

    public CustomEncoder(Encoder delegate) {
        this.delegate = delegate;
    }

    public CustomEncoder(Encoder delegate, String[] beanBasePackages) {
        this.delegate = delegate;
        this.beanBasePackages = beanBasePackages;
    }

    /**
     * @see feign.ReflectiveFeign.BuildEncodedTemplateFromArgs#resolve(Object[], RequestTemplate, Map)
     */
    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        if (bodyType == Object[].class) {
            Object[] objects = (Object[]) object;
            Map<String, Object> map = BeanPathMapUtil.toMapIn(beanBasePackages, objects);
            map.forEach((k, v) -> template.query(k, Objects.toString(v, null)));
        } else {
            delegate.encode(object, bodyType, template);
        }
    }
}
