package org.zero.common.api.extra.loki;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.QueryMapEncoder;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.zero.common.api.extra.loki.model.common.LokiMatrix;
import org.zero.common.api.extra.loki.model.common.LokiStream;
import org.zero.common.api.extra.loki.model.common.LokiVector;
import org.zero.common.api.extra.loki.model.request.LokiConfigRequest;
import org.zero.common.api.extra.loki.model.request.LokiDeleteCancelRequest;
import org.zero.common.api.extra.loki.model.request.LokiDeleteRequest;
import org.zero.common.api.extra.loki.model.request.LokiIngesterShutdownRequest;
import org.zero.common.api.extra.loki.model.request.LokiLogLevelRequest;
import org.zero.common.api.extra.loki.model.request.LokiSeriesRequest;
import org.zero.common.api.extra.loki.model.response.LokiQueryRangeResponse;
import org.zero.common.api.extra.loki.model.response.LokiQueryResponse;
import org.zero.common.api.extra.loki.model.response.LokiResponse;
import org.zero.common.api.extra.loki.model.response.LokiSeriesResponse;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Loki Feign 编解码配置测试。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/19
 */
class LokiFeignConfigTest {

    @Test
    void shouldEncodeFormFieldsWithOfficialNames() throws Exception {
        CapturingEncoder capturingEncoder = new CapturingEncoder();
        LokiFeignConfig.LokiFeignEncoder encoder = new LokiFeignConfig.LokiFeignEncoder(capturingEncoder, new ObjectMapper());

        encoder.encode(LokiLogLevelRequest.builder().logLevel("debug").build(),
                LokiLogLevelRequest.class, new RequestTemplate());
        Map<String, Object> logLevelMap = capturingEncoder.lastMap();
        assertEquals("debug", logLevelMap.get("log_level"));
        assertFalse(logLevelMap.containsKey("logLevel"));

        encoder.encode(LokiIngesterShutdownRequest.builder()
                        .flush(Boolean.TRUE)
                        .deleteRingTokens(Boolean.TRUE)
                        .terminate(Boolean.FALSE)
                        .build(),
                LokiIngesterShutdownRequest.class, new RequestTemplate());
        Map<String, Object> shutdownMap = capturingEncoder.lastMap();
        assertEquals(Boolean.TRUE, shutdownMap.get("delete_ring_tokens"));
        assertFalse(shutdownMap.containsKey("deleteRingTokens"));

        encoder.encode(LokiSeriesRequest.builder()
                        .match(new String[]{"{app=\"demo\"}"})
                        .since("5m")
                        .build(),
                LokiSeriesRequest.class, new RequestTemplate());
        Map<String, Object> seriesMap = capturingEncoder.lastMap();
        assertTrue(seriesMap.containsKey("match[]"));
        assertFalse(seriesMap.containsKey("match"));
    }

    @Test
    void shouldEncodeQueryMapFieldsWithOfficialNames() {
        LokiFeignConfig.LokiQueryMapEncoder encoder = new LokiFeignConfig.LokiQueryMapEncoder(new QueryMapEncoder.Default());

        Map<String, Object> seriesMap = encoder.encode(LokiSeriesRequest.builder()
                .match(new String[]{"{job=\"demo\"}", "{app=\"demo\"}"})
                .since("15m")
                .build());
        assertTrue(seriesMap.containsKey("match[]"));
        assertFalse(seriesMap.containsKey("match"));
        assertArrayValue(seriesMap.get("match[]"), "{job=\"demo\"}", "{app=\"demo\"}");

        Map<String, Object> deleteMap = encoder.encode(LokiDeleteRequest.builder()
                .query("{app=\"demo\"}")
                .start("2026-03-19T00:00:00Z")
                .maxInterval("1h")
                .build());
        assertEquals("1h", deleteMap.get("max_interval"));
        assertFalse(deleteMap.containsKey("maxInterval"));

        Map<String, Object> cancelMap = encoder.encode(LokiDeleteCancelRequest.builder()
                .requestId("req-1")
                .force(Boolean.TRUE)
                .build());
        assertEquals("req-1", cancelMap.get("request_id"));
        assertFalse(cancelMap.containsKey("requestId"));
    }

    @Test
    void shouldDeserializeVectorQueryResponse() throws Exception {
        String json = "{\"status\":\"success\",\"data\":{\"resultType\":\"vector\",\"result\":[{\"metric\":{\"job\":\"demo\"},\"value\":[3000000000,\"42\"]}],\"stats\":{\"summary\":{\"bytesProcessedPerSecond\":1}}}}";
        LokiFeignConfig.LokiFeignDecoder decoder = new LokiFeignConfig.LokiFeignDecoder(noopDecoder(), new ObjectMapper());

        @SuppressWarnings("unchecked")
        LokiResponse<LokiQueryResponse> response = (LokiResponse<LokiQueryResponse>) decoder.decode(
                jsonResponse(json),
                ResolvableType.forClassWithGenerics(LokiResponse.class, LokiQueryResponse.class).getType());

        assertTrue(response.isSuccess());
        LokiVector vector = (LokiVector) response.getData().getResult().iterator().next();
        assertEquals(Long.valueOf(3000000000L), vector.getValue().getEpochSecond());
        assertEquals("42", vector.getValue().getMetricValue());
    }

    @Test
    void shouldDeserializeMatrixAndStreamQueryRangeResponse() throws Exception {
        LokiFeignConfig.LokiFeignDecoder decoder = new LokiFeignConfig.LokiFeignDecoder(noopDecoder(), new ObjectMapper());

        String matrixJson = "{\"status\":\"success\",\"data\":{\"resultType\":\"matrix\",\"result\":[{\"metric\":{\"job\":\"demo\"},\"values\":[[3000000001,\"0.5\"]]}],\"stats\":{\"summary\":{\"totalBytesProcessed\":10}}}}";
        @SuppressWarnings("unchecked")
        LokiResponse<LokiQueryRangeResponse> matrixResponse = (LokiResponse<LokiQueryRangeResponse>) decoder.decode(
                jsonResponse(matrixJson),
                ResolvableType.forClassWithGenerics(LokiResponse.class, LokiQueryRangeResponse.class).getType());
        LokiMatrix matrix = (LokiMatrix) matrixResponse.getData().getResult().iterator().next();
        LokiMatrix.Value matrixValue = matrix.getValues().iterator().next();
        assertEquals(Long.valueOf(3000000001L), matrixValue.getEpochSecond());
        assertEquals("0.5", matrixValue.getMetricValue());

        String streamJson = "{\"status\":\"success\",\"data\":{\"resultType\":\"streams\",\"result\":[{\"stream\":{\"job\":\"demo\"},\"values\":[[\"1710000000000000000\",\"line one\",{\"trace_id\":\"trace-1\"}]]}],\"stats\":{\"summary\":{\"totalLinesProcessed\":1}}}}";
        @SuppressWarnings("unchecked")
        LokiResponse<LokiQueryRangeResponse> streamResponse = (LokiResponse<LokiQueryRangeResponse>) decoder.decode(
                jsonResponse(streamJson),
                ResolvableType.forClassWithGenerics(LokiResponse.class, LokiQueryRangeResponse.class).getType());
        LokiStream stream = (LokiStream) streamResponse.getData().getResult().iterator().next();
        LokiStream.Value streamValue = stream.getValues().iterator().next();
        assertEquals("line one", streamValue.getLogLine());
        assertEquals("trace-1", streamValue.getStructuredMetadata().get("trace_id"));
    }

    @Test
    void shouldDeserializeDynamicSeriesLabels() throws Exception {
        String json = "{\"status\":\"success\",\"data\":[{\"job\":\"demo\",\"custom_label\":\"custom-value\"}]}";
        LokiFeignConfig.LokiFeignDecoder decoder = new LokiFeignConfig.LokiFeignDecoder(noopDecoder(), new ObjectMapper());
        Type type = ResolvableType.forClassWithGenerics(LokiResponse.class,
                ResolvableType.forClassWithGenerics(Collection.class, LokiSeriesResponse.class)).getType();

        @SuppressWarnings("unchecked")
        LokiResponse<Collection<LokiSeriesResponse>> response = (LokiResponse<Collection<LokiSeriesResponse>>) decoder.decode(
                jsonResponse(json), type);

        LokiSeriesResponse seriesResponse = response.getData().iterator().next();
        assertEquals("demo", seriesResponse.get("job"));
        assertEquals("custom-value", seriesResponse.get("custom_label"));
    }

    @Test
    void shouldDeclareExpectedEndpointMappings() throws Exception {
        Method logLevelGet = LokiFeignClient.class.getMethod("logLevelGet");
        assertEquals(0, logLevelGet.getParameterCount());
        assertArrayEquals(new String[]{"/log_level"}, logLevelGet.getAnnotation(GetMapping.class).value());

        Method configWithMode = LokiFeignClient.class.getMethod("config", LokiConfigRequest.class);
        assertArrayEquals(new String[]{"/config"}, configWithMode.getAnnotation(GetMapping.class).value());

        Method ingesterShutdownPost = LokiFeignClient.class.getMethod("ingesterShutdownPost", LokiIngesterShutdownRequest.class);
        PostMapping ingesterShutdownPostMapping = ingesterShutdownPost.getAnnotation(PostMapping.class);
        assertArrayEquals(new String[]{"/ingester/shutdown"}, ingesterShutdownPostMapping.value());
        assertArrayEquals(new String[]{MediaType.APPLICATION_FORM_URLENCODED_VALUE}, ingesterShutdownPostMapping.consumes());

        Method deletePut = LokiFeignClient.class.getMethod("deletePut", LokiDeleteRequest.class);
        assertArrayEquals(new String[]{LokiFeignClient.API_V1_PATH + "/delete"}, deletePut.getAnnotation(PutMapping.class).value());

        Method deleteGet = LokiFeignClient.class.getMethod("deleteGet");
        assertArrayEquals(new String[]{LokiFeignClient.API_V1_PATH + "/delete"}, deleteGet.getAnnotation(GetMapping.class).value());

        Method deleteCancel = LokiFeignClient.class.getMethod("deleteCancel", LokiDeleteCancelRequest.class);
        assertArrayEquals(new String[]{LokiFeignClient.API_V1_PATH + "/delete"}, deleteCancel.getAnnotation(DeleteMapping.class).value());
    }

    private Decoder noopDecoder() {
        return (response, type) -> null;
    }

    private Response jsonResponse(String body) {
        Request request = Request.create(Request.HttpMethod.GET, "http://localhost",
                Collections.emptyMap(), (Request.Body) null, new RequestTemplate());
        return Response.builder()
                .status(200)
                .reason("OK")
                .request(request)
                .headers(Collections.emptyMap())
                .body(body, StandardCharsets.UTF_8)
                .build();
    }

    private void assertArrayValue(Object value, String... expected) {
        assertNotNull(value);
        if (value instanceof String[]) {
            assertArrayEquals(expected, (String[]) value);
            return;
        }
        if (value instanceof Collection<?>) {
            assertArrayEquals(expected, ((Collection<?>) value).toArray(new String[0]));
            return;
        }
        throw new AssertionError("Unsupported array value type: " + value.getClass());
    }

    private static class CapturingEncoder implements Encoder {
        private Object lastObject;

        @Override
        public void encode(Object object, Type bodyType, RequestTemplate template) {
            this.lastObject = object;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> lastMap() {
            return (Map<String, Object>) this.lastObject;
        }
    }
}
