package org.zero.common.core.extension.jackson;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Java 8 本地日期时间类型处理（全局）。
 * <p>
 * 这里只接管 {@link LocalDate}、{@link LocalTime}、{@link LocalDateTime} 三种不带时区语义的类型，
 * 让它们可以分别使用 {@code system.json.dateFormat}、{@code system.json.timeFormat}、
 * {@code system.json.datetimeFormat} 进行全局格式化。
 * <p>
 * 其他带时区或绝对时间语义的类型，例如 {@code OffsetDateTime}、{@code ZonedDateTime}、
 * {@code Instant} 以及 {@link java.util.Date}，统一交由 Spring Boot / Jackson 默认配置处理，
 * 避免用不含时区的自定义格式破坏其原始语义。
 * <p>
 * 单个字段处理参见：{@linkplain com.fasterxml.jackson.annotation.JsonFormat @JsonFormat}
 *
 * @author Zero (cnzeropro@163.com)
 * @see com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
 * @since 2024/10/16
 */
@RequiredArgsConstructor
public class JavaTimeJackson2ObjectMapperBuilderCustomizer implements Jackson2ObjectMapperBuilderCustomizer {
	private final JsonJavaTimeProperties jsonJavaTimeProperties;

	@Override
	public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
		// LocalDate / LocalTime / LocalDateTime 本身不携带时区信息，
		// 适合使用系统统一约定的文本格式进行序列化与反序列化。
		String datetimeFormat = jsonJavaTimeProperties.getDatetimeFormat();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datetimeFormat);
		String dateFormat = jsonJavaTimeProperties.getDateFormat();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
		String timeFormat = jsonJavaTimeProperties.getTimeFormat();
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(timeFormat);

		// 这里只覆盖本地日期时间类型；其他类型保留 JavaTimeModule 与 Spring Boot 默认行为。
		jacksonObjectMapperBuilder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
		jacksonObjectMapperBuilder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
		jacksonObjectMapperBuilder.serializerByType(LocalDate.class, new LocalDateSerializer(dateFormatter));
		jacksonObjectMapperBuilder.deserializerByType(LocalDate.class, new LocalDateDeserializer(dateFormatter));
		jacksonObjectMapperBuilder.serializerByType(LocalTime.class, new LocalTimeSerializer(timeFormatter));
		jacksonObjectMapperBuilder.deserializerByType(LocalTime.class, new LocalTimeDeserializer(timeFormatter));
	}
}
