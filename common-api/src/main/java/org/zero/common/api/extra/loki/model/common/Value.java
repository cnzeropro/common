package org.zero.common.api.extra.loki.model.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Value extends ArrayList<Object> {
    /**
     * 构建{@link Value}
     *
     * @param logLine log line
     */
    public static Value of(String logLine) {
        Instant now = Instant.now();
        long epochNano = now.getEpochSecond() * 1_000_000_000L + now.getNano();
        return of(epochNano, logLine);
    }

    /**
     * 构建{@link Value}
     *
     * @param epochNano unix epoch in nanoseconds
     * @param logLine   log line
     */
    public static Value of(long epochNano, String logLine) {
        Value value = new Value();
        value.add(String.valueOf(epochNano));
        value.add(logLine);
        return value;
    }

    /**
     * 构建{@link Value}
     *
     * @param epochNano unix epoch in nanoseconds
     * @param logLine   log line
     * @param metadata  structured metadata
     */
    public static Value of(long epochNano, String logLine, Map<String, Object> metadata) {
        Value value = new Value(3);
        value.add(String.valueOf(epochNano));
        value.add(logLine);
        value.add(metadata);
        return value;
    }

    public Value() {
        this(2);
    }

    protected Value(int initialCapacity) {
        super(initialCapacity);
    }
}
