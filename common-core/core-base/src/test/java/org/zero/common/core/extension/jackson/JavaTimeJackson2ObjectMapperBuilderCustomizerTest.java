package org.zero.common.core.extension.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
class JavaTimeJackson2ObjectMapperBuilderCustomizerTest {
	@Test
	void shouldSerializeAndDeserializeLocalJavaTimeWithConfiguredFormats() throws Exception {
		ObjectMapper objectMapper = createObjectMapper("yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd", "HH:mm:ss");
		LocalDate localDate = LocalDate.of(2026, 4, 15);
		LocalTime localTime = LocalTime.of(10, 20, 30);
		LocalDateTime localDateTime = LocalDateTime.of(2026, 4, 15, 10, 20, 30);

		assertEquals("\"2026/04/15\"", objectMapper.writeValueAsString(localDate));
		assertEquals("\"10:20:30\"", objectMapper.writeValueAsString(localTime));
		assertEquals("\"2026/04/15 10:20:30\"", objectMapper.writeValueAsString(localDateTime));
		assertEquals(localDate, objectMapper.readValue("\"2026/04/15\"", LocalDate.class));
		assertEquals(localTime, objectMapper.readValue("\"10:20:30\"", LocalTime.class));
		assertEquals(localDateTime, objectMapper.readValue("\"2026/04/15 10:20:30\"", LocalDateTime.class));
	}

	@Test
	void shouldRoundTripZonedDateTimeWithoutChangingZone() throws Exception {
		ObjectMapper objectMapper = createObjectMapper("yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd", "HH:mm:ss");
		ZonedDateTime zonedDateTime = ZonedDateTime.of(2026, 4, 15, 10, 20, 30, 123000000, ZoneId.of("Asia/Shanghai"));

		String json = objectMapper.writeValueAsString(zonedDateTime);
		ZonedDateTime actual = objectMapper.readValue(json, ZonedDateTime.class);

		assertEquals("\"" + zonedDateTime.toOffsetDateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + "\"", json);
		assertEquals(zonedDateTime.toInstant(), actual.toInstant());
	}

	@Test
	void shouldRoundTripOffsetDateTimeWithoutChangingOffset() throws Exception {
		ObjectMapper objectMapper = createObjectMapper("yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd", "HH:mm:ss");
		OffsetDateTime offsetDateTime = OffsetDateTime.of(2026, 4, 15, 10, 20, 30, 123000000, ZoneOffset.ofHours(8));

		String json = objectMapper.writeValueAsString(offsetDateTime);
		OffsetDateTime actual = objectMapper.readValue(json, OffsetDateTime.class);

		assertEquals("\"" + offsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + "\"", json);
		assertEquals(offsetDateTime.toInstant(), actual.toInstant());
	}

	@Test
	void shouldUseSpringDateFormatForDateInsteadOfLocalJavaTimeFormat() throws Exception {
		JsonJavaTimeProperties properties = new JsonJavaTimeProperties();
		properties.setDatetimeFormat("yyyy-MM-dd HH:mm:ss");
		properties.setDateFormat("yyyy-MM-dd");
		properties.setTimeFormat("HH:mm:ss");

		Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
		builder.modulesToInstall(new JavaTimeModule());
		builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		builder.simpleDateFormat("yyyy/MM/dd HH:mm:ss");
		builder.timeZone(TimeZone.getTimeZone("UTC"));
		new JavaTimeJackson2ObjectMapperBuilderCustomizer(properties).customize(builder);
		ObjectMapper objectMapper = builder.build();

		assertEquals("\"1970/01/01 00:00:00\"", objectMapper.writeValueAsString(new Date(0L)));
		assertEquals("\"2026-04-15 10:20:30\"", objectMapper.writeValueAsString(LocalDateTime.of(2026, 4, 15, 10, 20, 30)));
	}

	private ObjectMapper createObjectMapper(String datetimeFormat, String dateFormat, String timeFormat) {
		JsonJavaTimeProperties properties = new JsonJavaTimeProperties();
		properties.setDatetimeFormat(datetimeFormat);
		properties.setDateFormat(dateFormat);
		properties.setTimeFormat(timeFormat);

		Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
		builder.modulesToInstall(new JavaTimeModule());
		builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		new JavaTimeJackson2ObjectMapperBuilderCustomizer(properties).customize(builder);
		return builder.build();
	}
}
