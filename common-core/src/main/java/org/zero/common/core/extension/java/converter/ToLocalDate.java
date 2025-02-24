package org.zero.common.core.extension.java.converter;

import cn.hutool.core.date.DateUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.time.zone.ZoneRules;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class ToLocalDate implements GenericConverter<LocalDate> {
    public static final ToLocalDate INSTANCE = new ToLocalDate();

    @Override
    public LocalDate convert(Object source) {
        if (source instanceof LocalDate) {
            return (LocalDate) source;
        }
        if (source instanceof Instant) {
            // copy from Java 9 java.time.LocalDate.ofInstant
            Instant instant = (Instant) source;
            ZoneRules zoneRules = ZoneId.systemDefault().getRules();
            ZoneOffset zoneOffset = zoneRules.getOffset(instant);
            long localSecond = instant.getEpochSecond() + zoneOffset.getTotalSeconds();
            long localEpochDay = Math.floorDiv(localSecond, 60 * 60 * 24L);
            return LocalDate.ofEpochDay(localEpochDay);
        }
        if (source instanceof TemporalAccessor) {
            return LocalDate.from((TemporalAccessor) source);
        }
        if (Objects.nonNull(source)) {
            String string = source.toString();
            // 尝试解析为日期（Hutool）
            return DateUtil.parse(string).toLocalDateTime().toLocalDate();
        }
        return null;
    }
}
