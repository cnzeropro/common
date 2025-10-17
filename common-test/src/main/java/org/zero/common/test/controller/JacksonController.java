package org.zero.common.test.controller;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zero.common.core.extension.jackson.databind.ser.NumberJsonComponent;
import org.zero.common.data.model.view.Result;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/8
 */
@RestController
@RequestMapping("/jackson")
public class JacksonController {
    /**
     * @see org.zero.common.core.extension.jackson.databind.ser.JSONNullSerializer
     */
    @RequestMapping("/null")
    public Result<JSON> jsonNullType() {
        JSONConfig jsonConfig = JSONConfig.create()
                .setIgnoreNullValue(false);
        JSONObject jsonObject = JSONUtil.createObj(jsonConfig)
                .set("id", 12345L)
                .set("name", "Bob")
                .set("age", null);
        return Result.ok(jsonObject);
    }

    /**
     * @see org.zero.common.core.extension.jackson.JavaTimeJackson2ObjectMapperBuilderCustomizer
     */
    @RequestMapping("/datetime")
    public Result<Map<String, Serializable>> datetimeType() {
        Map<String, Serializable> datetimeMap = MapBuilder.<String, Serializable>create(true)
                .put("date", new java.util.Date())
                .put("sqlDate", new java.sql.Date(System.currentTimeMillis()))
                .put("sqlTime", new java.sql.Time(System.currentTimeMillis()))
                .put("sqlTimestamp", new java.sql.Timestamp(System.currentTimeMillis()))
                .put("calendar", java.util.Calendar.getInstance())
                .put("timeZone", java.util.TimeZone.getDefault())
                .put("localDate", java.time.LocalDate.now())
                .put("localTime", java.time.LocalTime.now())
                .put("localDateTime", java.time.LocalDateTime.now())
                .put("zonedDateTime", java.time.ZonedDateTime.now())
                .put("offsetDateTime", java.time.OffsetDateTime.now())
                .put("offsetTime", java.time.OffsetTime.now())
                .put("instant", java.time.Instant.now())
                .put("duration", java.time.Duration.ZERO)
                .put("yearMonth", java.time.YearMonth.now())
                .put("year", java.time.Year.now())
                .put("monthDay", java.time.MonthDay.now())
                .put("period", java.time.Period.ZERO)
                .put("japaneseDate", java.time.chrono.JapaneseDate.now())
                .put("thaiBuddhistDate", java.time.chrono.ThaiBuddhistDate.now())
                .put("hijrahDate", java.time.chrono.HijrahDate.now())
                .put("minguoDate", java.time.chrono.MinguoDate.now())
                .put("zoneId", java.time.ZoneId.systemDefault())
                .put("zoneOffset", java.time.ZoneOffset.UTC)
                .build();
        return Result.ok(datetimeMap);
    }

    /**
     * @see NumberJsonComponent.LongSerializer
     */
    @RequestMapping("/long")
    public Result<Long> longType() {
        return Result.ok(100L);
    }

    /**
     * @see NumberJsonComponent.DoubleSerializer
     */
    @RequestMapping("/double")
    public Result<Double> doubleType() {
        return Result.ok(Double.MAX_VALUE);
    }
}
