package org.zero.common.core.support.bean.query;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.zero.common.core.support.bean.map.BeanEvaluator;
import org.zero.common.core.support.bean.map.BeanMap;
import org.zero.common.core.support.bean.map.DefaultBeanEvaluator;
import org.zero.common.core.support.bean.map.ObjectConfig;
import org.zero.common.core.support.bean.map.ValueToParamStringHandler;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * 使用方式：<br>
 * 1、注入 {@link QueryBean} 参数处理器（{@link QueryBeanParameterProcessor}）到 Spring 容器<br>
 * 2、为 FeignClient 指定自定义 Encoder（{@link QueryBeanEncoder}）<br>
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
public class QueryBeanEncoder implements Encoder {
	protected final Encoder delegate;
	protected final BeanEvaluator beanEvaluator;

	public QueryBeanEncoder(Encoder delegate) {
		this(delegate, DefaultBeanEvaluator.INSTANCE);
	}

	public QueryBeanEncoder(Encoder delegate, BeanEvaluator beanEvaluator) {
		this.delegate = delegate;
		this.beanEvaluator = beanEvaluator;
	}

	/**
	 * @see feign.ReflectiveFeign.BuildEncodedTemplateFromArgs#resolve(Object[], RequestTemplate, Map)
	 */
	@Override
	public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
		if (bodyType == Object[].class) {
			Object[] objects = (Object[]) object;
			ObjectConfig objectConfig = ObjectConfig.of(objects)
				.prefix(null);
			Map<String, String> map = BeanMap.of(new ValueToParamStringHandler())
				.objectConfig(objectConfig)
				.beanEvaluator(beanEvaluator)
				.toMap();
			map.forEach(template::query);
		} else {
			delegate.encode(object, bodyType, template);
		}
	}
}
