package org.zero.common.core.extension.jackson;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.format.DateTimeFormatter;

/**
 * Java 8 日期时间类型处理（全局）
 * <p>
 * 单个处理参见：{@linkplain  com.fasterxml.jackson.annotation.JsonFormat @JsonFormat}
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
        // 不建议使用 java.time.format.DateTimeFormatter.withZone 更改时区
        // 因为 LocalDateTime、LocalTime 等等本来就无时区信息，所以即使使用带时区的 DateTimeFormatter 也没有任何作用
        // 但 ZonedDateTime、OffsetDateTime 等等天然带时区信息，贸然更改时区会破坏其语义
        String datetimeFormat = jsonJavaTimeProperties.getDatetimeFormat();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datetimeFormat);
        String dateFormat = jsonJavaTimeProperties.getDateFormat();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
        String timeFormat = jsonJavaTimeProperties.getTimeFormat();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(timeFormat);
        jacksonObjectMapperBuilder.serializers(new LocalDateTimeSerializer(dateTimeFormatter),
                new LocalDateSerializer(dateFormatter),
                new LocalTimeSerializer(timeFormatter),
                new ZonedDateTimeSerializer(dateTimeFormatter));
    }
}
