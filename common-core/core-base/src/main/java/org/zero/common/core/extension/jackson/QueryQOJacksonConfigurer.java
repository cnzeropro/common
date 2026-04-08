package org.zero.common.core.extension.jackson;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.zero.common.core.support.query.PageParameterProvider;
import org.zero.common.data.model.query.ConditionGroupQO;
import org.zero.common.data.model.query.ConditionQO;
import org.zero.common.data.model.query.PageQO;
import org.zero.common.data.model.query.PredicateQO;
import org.zero.common.data.model.query.QueryQO;
import org.zero.common.data.model.query.ReportQO;

/**
 * QueryQO 的 Jackson 适配配置。
 * <p>
 * 该配置是可选的，仅在使用 Jackson 序列化 / 反序列化 QueryQO 条件树时启用。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/2
 */
public final class QueryQOJacksonConfigurer {
	private QueryQOJacksonConfigurer() {
	}

	public static void configure(ObjectMapper objectMapper) {
		if (objectMapper == null) {
			throw new IllegalArgumentException("ObjectMapper must not be null");
		}
		objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
		objectMapper.addMixIn(PredicateQO.class, PredicateQOMixIn.class);
		objectMapper.addMixIn(QueryQO.class, QueryQOMixIn.class);
		objectMapper.addMixIn(ReportQO.class, ReportQOMixIn.class);
	}

	public static void configure(Jackson2ObjectMapperBuilder builder) {
		if (builder == null) {
			throw new IllegalArgumentException("Jackson2ObjectMapperBuilder must not be null");
		}
		builder.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
		builder.mixIn(PredicateQO.class, PredicateQOMixIn.class);
		builder.mixIn(QueryQO.class, QueryQOMixIn.class);
		builder.mixIn(ReportQO.class, ReportQOMixIn.class);
	}

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
	@JsonSubTypes({
		@JsonSubTypes.Type(value = ConditionQO.class, name = "condition"),
		@JsonSubTypes.Type(value = ConditionGroupQO.class, name = "group")
	})
	private interface PredicateQOMixIn {
	}

	@JsonDeserialize(as = PageCompatibleQueryQO.class)
	private interface QueryQOMixIn {
	}

	@JsonDeserialize(as = PageCompatibleReportQO.class)
	private interface ReportQOMixIn {
	}

	public static class PageCompatibleQueryQO extends QueryQO implements PageParameterProvider {
		private static final long serialVersionUID = 1L;

		private long number = PageQO.DEFAULT_NUMBER;
		private long size = PageQO.DEFAULT_SIZE;

		public long getNumber() {
			return number;
		}

		public void setNumber(long number) {
			this.number = number;
		}

		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}
	}

	public static class PageCompatibleReportQO extends ReportQO implements PageParameterProvider {
		private static final long serialVersionUID = 1L;

		private long number = PageQO.DEFAULT_NUMBER;
		private long size = PageQO.DEFAULT_SIZE;

		public long getNumber() {
			return number;
		}

		public void setNumber(long number) {
			this.number = number;
		}

		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}
	}
}
