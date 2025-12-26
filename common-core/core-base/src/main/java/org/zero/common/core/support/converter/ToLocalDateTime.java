package org.zero.common.core.support.converter;

import cn.hutool.core.date.DateUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class ToLocalDateTime implements ObjectConverter<LocalDateTime> {
    public static final ToLocalDateTime INSTANCE = new ToLocalDateTime();

    @Override
    public LocalDateTime convert(Object source) {
        if (source instanceof LocalDateTime) {
            return (LocalDateTime) source;
        }
        if (source instanceof Instant) {
            return LocalDateTime.ofInstant((Instant) source, ZoneId.systemDefault());
        }
        if (source instanceof TemporalAccessor) {
            return LocalDateTime.from((TemporalAccessor) source);
        }
        if (Objects.nonNull(source)) {
            String string = source.toString();
            // 尝试解析为日期（Hutool）
            return DateUtil.parse(string).toLocalDateTime();
        }
        return null;
    }
}
