package org.zero.common.core.extension.jackson;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * QueryQO 的 Jackson Builder 自定义器。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public class QueryQOJackson2ObjectMapperBuilderCustomizer implements Jackson2ObjectMapperBuilderCustomizer {
	@Override
	public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
		QueryQOJacksonConfigurer.configure(jacksonObjectMapperBuilder);
	}
}
