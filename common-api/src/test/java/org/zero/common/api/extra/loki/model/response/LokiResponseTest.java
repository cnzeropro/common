package org.zero.common.api.extra.loki.model.response;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.zero.common.api.extra.loki.constant.ResultType;
import org.zero.common.api.extra.loki.model.common.LokiStats;
import org.zero.common.api.extra.loki.model.common.LokiStream;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/27
 */
class LokiResponseTest {

    @SneakyThrows
    @Test
    void test() {
        Instant now = Instant.now();
        BigInteger epochSecond = BigInteger.valueOf(now.getEpochSecond());
        BigInteger nanosPerSecond = BigInteger.valueOf(TimeUnit.SECONDS.toNanos(1));
        BigInteger nano = BigInteger.valueOf(now.getNano());
        BigInteger epochNano = epochSecond.multiply(nanosPerSecond).add(nano);
        System.out.println("epochNano: " + epochNano);
        LokiStream.Value value1 = new LokiStream.Value(epochNano.toString(),"log line test");
        LokiStream lokiStream = LokiStream.builder()
                .stream(Collections.singletonMap("a", "b"))
                .value(value1)
                .build();
        LokiResponse<LokiQueryRangeResponse> lokiResponse = LokiResponse.<LokiQueryRangeResponse>builder()
                .status("success")
                .data(LokiQueryRangeResponse.builder()
                        .resultType(ResultType.STREAMS)
                        .result(Collections.singletonList(lokiStream))
                        .stats(LokiStats.builder()
                                .summary(LokiStats.Summary.builder()
                                        .bytesProcessedPerSecond(1L)
                                        .build())
                                .build())
                        .build())
                .build();

        System.out.println("lokiResult: " + lokiResponse);
    }
}