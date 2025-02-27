package org.zero.common.core.extension.java.converter;

import cn.hutool.core.date.DateUtil;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.time.zone.ZoneRules;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class ToLocalTime implements GenericConverter<LocalTime> {
    public static final ToLocalTime INSTANCE = new ToLocalTime();

    @Override
    public LocalTime convert(Object source) {
        if (source instanceof LocalTime) {
            return (LocalTime) source;
        }
        if (source instanceof Instant) {
            // copy from Java 9 java.time.LocalTime.ofInstant
            Instant instant = (Instant) source;
            ZoneRules zoneRules = ZoneId.systemDefault().getRules();
            ZoneOffset zoneOffset = zoneRules.getOffset(instant);
            long localSecond = instant.getEpochSecond() + zoneOffset.getTotalSeconds();
            long secsOfDay = Math.floorMod(localSecond, 60 * 60 * 24L);
            return LocalTime.ofNanoOfDay(secsOfDay * 1000_000_000L + instant.getNano());
        }
        if (source instanceof TemporalAccessor) {
            return LocalTime.from((TemporalAccessor) source);
        }
        if (Objects.nonNull(source)) {
            String string = source.toString();
            // 尝试解析为日期（Hutool）
            return DateUtil.parse(string).toLocalDateTime().toLocalTime();
        }
        return null;
    }
}
