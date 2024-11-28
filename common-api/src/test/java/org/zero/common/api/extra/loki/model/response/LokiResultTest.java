package org.zero.common.api.extra.loki.model.response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.zero.common.api.extra.loki.model.common.LokiStats;
import org.zero.common.api.extra.loki.model.common.LokiStream;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/27
 */
class LokiResultTest {

    @SneakyThrows
    @Test
    void test() {
        LokiStream.Value value1 = new LokiStream.Value();
        Instant now = Instant.now();
        long epochNano = now.getEpochSecond() * 1_000_000_000L + now.getNano();
        System.out.println("epochNano: " + epochNano);
        value1.add(String.valueOf(epochNano));
        value1.add("log line");
        LokiStream lokiStream = LokiStream.builder().stream(Collections.singletonMap("a", "b"))
                .value(value1)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String lokiStreamJsonStr = objectMapper.writeValueAsString(lokiStream);
        System.out.println("lokiStreamJsonStr: " + lokiStreamJsonStr);
        Map<String, Object> lokiStreamMap = objectMapper.readValue(lokiStreamJsonStr, new TypeReference<Map<String, Object>>() {
        });
        LokiResult<LokiQueryRangeResponse> lokiResult = LokiResult.<LokiQueryRangeResponse>builder()
                .status("success")
                .data(LokiQueryRangeResponse.builder()
                        .resultType("streams")
                        .result(Collections.singletonList(lokiStreamMap))
                        .stats(LokiStats.builder()
                                .summary(Collections.singletonMap("bytes", 1))
                                .build())
                        .build())
                .build();

        System.out.println("lokiResult: " + lokiResult);
        String lokiResultJsonStr = objectMapper.writeValueAsString(lokiResult);
        System.out.println("lokiResultJsonStr: " + lokiResultJsonStr);
    }
}