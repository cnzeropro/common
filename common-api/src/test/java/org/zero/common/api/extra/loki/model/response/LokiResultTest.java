package org.zero.common.api.extra.loki.model.response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.zero.common.api.extra.loki.model.common.Stats;
import org.zero.common.api.extra.loki.model.common.Stream;

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
        Stream.Value value1 = new Stream.Value();
        Instant now = Instant.now();
        long epochNano = now.getEpochSecond() * 1_000_000_000L + now.getNano();
        value1.add(String.valueOf(epochNano));
        value1.add("log line");
        Stream stream = Stream.builder().stream(Collections.singletonMap("a", "b"))
                .value(value1)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String serialize = objectMapper.writeValueAsString(stream);
        System.out.println(serialize);
        Map<String, Object> map = objectMapper.readValue(serialize, new TypeReference<Map<String, Object>>() {
        });
        LokiResult<LokiQueryRangeResponse> lokiResult = LokiResult.<LokiQueryRangeResponse>builder()
                .data(LokiQueryRangeResponse.builder()
                        .result(Collections.singletonList(map))
                        .resultType("streams")
                        .stats(Stats.builder().build())
                        .build())
                .status("success")
                .build();

        String lokiResultJsonStr = objectMapper.writeValueAsString(lokiResult);
        System.out.println(lokiResultJsonStr);
    }
}