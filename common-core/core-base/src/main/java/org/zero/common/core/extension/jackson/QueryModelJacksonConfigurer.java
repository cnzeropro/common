package org.zero.common.core.extension.jackson;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.zero.common.core.support.query.PageParameterProvider;
import org.zero.common.data.model.query.FilterConditionQO;
import org.zero.common.data.model.query.FilterGroupQO;
import org.zero.common.data.model.query.FilterQO;
import org.zero.common.data.model.query.ListQO;
import org.zero.common.data.model.query.PageQO;
import org.zero.common.data.model.query.ReportQO;

/**
 * 查询模型的 Jackson 适配配置。
 * <p>
 * 该配置是可选的，仅在使用 Jackson 序列化 / 反序列化查询条件树时启用。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public class QueryModelJacksonConfigurer implements Jackson2ObjectMapperBuilderCustomizer {

	public static void configure(ObjectMapper objectMapper) {
		if (objectMapper == null) {
			throw new IllegalArgumentException("ObjectMapper must not be null");
		}
		objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
		objectMapper.addMixIn(FilterQO.class, FilterQOMixIn.class);
		objectMapper.addMixIn(ListQO.class, ListQOMixIn.class);
		objectMapper.addMixIn(ReportQO.class, ReportQOMixIn.class);
	}

	public static void configure(Jackson2ObjectMapperBuilder builder) {
		if (builder == null) {
			throw new IllegalArgumentException("Jackson2ObjectMapperBuilder must not be null");
		}
		builder.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
		builder.mixIn(FilterQO.class, FilterQOMixIn.class);
		builder.mixIn(ListQO.class, ListQOMixIn.class);
		builder.mixIn(ReportQO.class, ReportQOMixIn.class);
	}

	@Override
	public void customize(Jackson2ObjectMapperBuilder builder) {
		configure(builder);
	}

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
	@JsonSubTypes({
			@JsonSubTypes.Type(value = FilterConditionQO.class, name = "condition"),
			@JsonSubTypes.Type(value = FilterGroupQO.class, name = "group")
	})
	private interface FilterQOMixIn {
	}

	@JsonDeserialize(as = PageCompatibleListQO.class)
	private interface ListQOMixIn {
	}

	@JsonDeserialize(as = PageCompatibleReportQO.class)
	private interface ReportQOMixIn {
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class PageCompatibleListQO extends ListQO implements PageParameterProvider {
		private long number = PageQO.DEFAULT_NUMBER;
		private long size = PageQO.DEFAULT_SIZE;
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class PageCompatibleReportQO extends ReportQO implements PageParameterProvider {
		private long number = PageQO.DEFAULT_NUMBER;
		private long size = PageQO.DEFAULT_SIZE;
	}
}
