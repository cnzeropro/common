package org.zero.common.core.extension.jackson;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.TimeZone;

/**
 * JSON 本地日期时间格式配置。
 * <p>
 * 这组配置可供不同 JSON 组件共享使用，用于
 * {@link java.time.LocalDate}、{@link java.time.LocalTime}、
 * {@link java.time.LocalDateTime} 的全局格式化与反序列化，
 * 不用于 {@code OffsetDateTime}、{@code ZonedDateTime}、{@code Instant}
 * 或 {@link java.util.Date}。
 * <p>
 * 带时区类型和 {@link java.util.Date} 继续遵循具体 JSON 组件的原生规则，
 * 以保留其时区或绝对时间语义。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/8
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "system.json")
public class JsonJavaTimeProperties {
    /**
	 * 本地日期时间格式，对应 {@link java.time.LocalDateTime}。
	 * <p>
	 * 默认值与 {@code spring.jackson.date-format} 保持一致，但仅作用于本地日期时间类型。
     */
    @Value("${spring.jackson.date-format:#{null}}")
    private String datetimeFormat = "yyyy-MM-dd HH:mm:ss.SSS";
    /**
	 * 本地日期格式，对应 {@link java.time.LocalDate}。
     */
    private String dateFormat = "yyyy-MM-dd";
    /**
	 * 本地时间格式，对应 {@link java.time.LocalTime}。
     */
    private String timeFormat = "HH:mm:ss.SSS";
    /**
	 * Spring 默认 Jackson 时区配置映射。
     * <p>
	 * 当前自定义的本地日期时间格式化逻辑并不会使用该字段；
	 * 保留它只是为了与 Spring 的 {@code spring.jackson.time-zone} 配置保持语义对应。
     */
    @Value("${spring.jackson.time-zone:#{null}}")
    private TimeZone timeZone = TimeZone.getDefault();
}
