package org.zero.common.core.support.converter;

import cn.hutool.core.date.DateUtil;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class ToDate implements GenericConverter<Date> {
    public static final ToDate INSTANCE = new ToDate();

    @Override
    public Date convert(Object source) {
        if (source instanceof Date) {
            return (Date) source;
        }
        if (source instanceof Instant) {
            return Date.from((Instant) source);
        }
        if (source instanceof Number) {
            long num = ((Number) source).longValue();
            return new Date(num);
        }
        if (Objects.nonNull(source)) {
            String string = source.toString();
            // 尝试解析为日期（Hutool）
            return DateUtil.parse(string);
        }
        return null;
    }
}
