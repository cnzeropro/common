package org.zero.common.api.extra.loki.model.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LokiValue extends ArrayList<Object> {
    /**
     * 构建{@link LokiValue}
     *
     * @param logLine log line
     */
    public static LokiValue of(String logLine) {
        Instant now = Instant.now();
        BigInteger epochSecond = BigInteger.valueOf(now.getEpochSecond());
        BigInteger nanosPerSecond = BigInteger.valueOf(TimeUnit.SECONDS.toNanos(1));
        BigInteger nano = BigInteger.valueOf(now.getNano());
        BigInteger epochNano = epochSecond.multiply(nanosPerSecond).add(nano);
        return of(epochNano, logLine);
    }

    /**
     * 构建{@link LokiValue}
     *
     * @param epochNano unix epoch in nanoseconds
     * @param logLine   log line
     */
    public static LokiValue of(BigInteger epochNano, String logLine) {
        LokiValue value = new LokiValue();
        value.add(epochNano.toString());
        value.add(logLine);
        return value;
    }

    /**
     * 构建{@link LokiValue}
     *
     * @param epochNano unix epoch in nanoseconds
     * @param logLine   log line
     * @param metadata  structured metadata
     */
    public static LokiValue of(BigInteger epochNano, String logLine, Map<String, Object> metadata) {
        LokiValue value = new LokiValue(3);
        value.add(epochNano.toString());
        value.add(logLine);
        value.add(metadata);
        return value;
    }

    public LokiValue() {
        this(2);
    }

    protected LokiValue(int initialCapacity) {
        super(initialCapacity);
    }
}
