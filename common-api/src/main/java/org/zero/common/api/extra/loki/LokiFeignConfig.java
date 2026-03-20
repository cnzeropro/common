package org.zero.common.api.extra.loki;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import feign.FeignException;
import feign.QueryMapEncoder;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;
import org.springframework.util.TypeUtils;
import org.zero.common.api.extra.loki.constant.LokiResultType;
import org.zero.common.api.extra.loki.model.common.LokiMatrix;
import org.zero.common.api.extra.loki.model.common.LokiResult;
import org.zero.common.api.extra.loki.model.common.LokiStats;
import org.zero.common.api.extra.loki.model.common.LokiStream;
import org.zero.common.api.extra.loki.model.common.LokiVector;
import org.zero.common.api.extra.loki.model.request.LokiDeleteCancelRequest;
import org.zero.common.api.extra.loki.model.request.LokiDeleteRequest;
import org.zero.common.api.extra.loki.model.request.LokiIngesterShutdownRequest;
import org.zero.common.api.extra.loki.model.request.LokiLogLevelRequest;
import org.zero.common.api.extra.loki.model.request.LokiSeriesRequest;
import org.zero.common.api.extra.loki.model.response.LokiQueryRangeResponse;
import org.zero.common.api.extra.loki.model.response.LokiQueryResponse;
import org.zero.common.api.extra.loki.model.response.LokiResponse;
import org.zero.common.api.extra.loki.model.response.LokiSeriesResponse;
import org.zero.common.core.extension.feign.AbstractFeignConfig;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Loki Feign 自定义配置。
 * <p>
 * 用于处理 Loki HTTP API 中的特殊 query/form 参数名，以及 {@code resultType} 驱动的自定义响应反序列化。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/25
 */
@RequiredArgsConstructor
public class LokiFeignConfig extends AbstractFeignConfig {
    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<Map<String, Object>>() {
    };

    final ObjectMapper objectMapper;

    @Bean
    Encoder lokiFeignEncoder() {
        Encoder defaultEncoder = this.defaultEncoder();
        return new LokiFeignEncoder(defaultEncoder, objectMapper);
    }

    @Bean
    Decoder lokiFeignDecoder() {
        Decoder defaultDecoder = this.defaultDecoder();
        return new LokiFeignDecoder(defaultDecoder, objectMapper);
    }

    @Bean
    QueryMapEncoder lokiQueryMapEncoder() {
        QueryMapEncoder defaultQueryMapEncoder = this.defaultQueryMapEncoder();
        return new LokiQueryMapEncoder(defaultQueryMapEncoder);
    }

    private static void remapField(Map<String, Object> map, String sourceField, String targetField) {
        if (map.containsKey(sourceField)) {
            map.put(targetField, map.remove(sourceField));
        }
    }

    private static Map<String, Object> remapLokiFields(Object object, Map<String, Object> originalMap) {
        Map<String, Object> map = new LinkedHashMap<>(originalMap);
        if (object instanceof LokiLogLevelRequest) {
            remapField(map, "logLevel", "log_level");
        }
        if (object instanceof LokiIngesterShutdownRequest) {
            remapField(map, "deleteRingTokens", "delete_ring_tokens");
        }
        if (object instanceof LokiDeleteRequest) {
            remapField(map, "maxInterval", "max_interval");
        }
        if (object instanceof LokiDeleteCancelRequest) {
            remapField(map, "requestId", "request_id");
        }
        if (object instanceof LokiSeriesRequest) {
            remapField(map, "match", "match[]");
        }
        return map;
    }

    @RequiredArgsConstructor
    static class LokiFeignEncoder implements Encoder {
        final Encoder encoder;
        final ObjectMapper objectMapper;

        @Override
        public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
            if (requiresCustomFormEncoding(bodyType)) {
                Map<String, Object> map = objectMapper.convertValue(object, MAP_TYPE_REFERENCE);
                encoder.encode(remapLokiFields(object, map), MAP_STRING_WILDCARD, template);
                return;
            }
            encoder.encode(object, bodyType, template);
        }

        private boolean requiresCustomFormEncoding(Type bodyType) {
            return TypeUtils.isAssignable(LokiLogLevelRequest.class, bodyType)
                    || TypeUtils.isAssignable(LokiIngesterShutdownRequest.class, bodyType)
                    || TypeUtils.isAssignable(LokiSeriesRequest.class, bodyType);
        }
    }

    static class LokiFeignDecoder implements Decoder {
        final Decoder decoder;
        final ObjectMapper objectMapper;

        LokiFeignDecoder(Decoder decoder, ObjectMapper objectMapper) {
            this.decoder = decoder;
            SimpleModule module = new SimpleModule();
            module.addDeserializer(LokiQueryResponse.class, new LokiQueryResponseDeserializer(objectMapper));
            module.addDeserializer(LokiQueryRangeResponse.class, new LokiQueryRangeResponseDeserializer(objectMapper));
            this.objectMapper = objectMapper.copy().registerModule(module);
        }

        @Override
        public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
            if (TypeUtils.isAssignable(ResolvableType
                            .forClassWithGenerics(LokiResponse.class,
                                    ResolvableType.forClassWithGenerics(Collection.class, LokiSeriesResponse.class))
                            .getType(), type)
                    || TypeUtils.isAssignable(
                    ResolvableType.forClassWithGenerics(LokiResponse.class, LokiQueryResponse.class).getType(), type)
                    || TypeUtils.isAssignable(
                    ResolvableType.forClassWithGenerics(LokiResponse.class, LokiQueryRangeResponse.class).getType(),
                    type)) {
                Response.Body body = response.body();
                if (Objects.nonNull(body)) {
                    InputStream inputStream = body.asInputStream();
                    return objectMapper.readValue(inputStream, objectMapper.getTypeFactory().constructType(type));
                }
            }
            return decoder.decode(response, type);
        }

        static class LokiQueryResponseDeserializer extends StdDeserializer<LokiQueryResponse> {
            final ObjectMapper mapper;

            LokiQueryResponseDeserializer(ObjectMapper mapper) {
                super(LokiQueryResponse.class);
                this.mapper = mapper;
            }

            @Override
            public LokiQueryResponse deserialize(JsonParser p, DeserializationContext ctxt)
                    throws IOException, JacksonException {
                JsonNode root = p.getCodec().readTree(p);
                LokiResultType resultType = Optional.ofNullable(root.get("resultType"))
                        .map(JsonNode::asText)
                        .map(LokiResultType::of)
                        .orElse(null);
                TypeFactory typeFactory = ctxt.getTypeFactory();
                JavaType javaType;
                if (LokiResultType.VECTOR == resultType) {
                    javaType = typeFactory.constructCollectionType(Collection.class, typeFactory.constructType(LokiVector.class));
                } else if (LokiResultType.STREAMS == resultType) {
                    javaType = typeFactory.constructCollectionType(Collection.class, typeFactory.constructType(LokiStream.class));
                } else {
                    throw new IllegalArgumentException("Unknown resultType: " + resultType);
                }
                Collection<? extends LokiResult> result = mapper.treeToValue(root.get("result"), javaType);
                LokiStats stats = mapper.treeToValue(root.get("stats"), LokiStats.class);
                return LokiQueryResponse.builder()
                        .resultType(resultType)
                        .result(result)
                        .stats(stats)
                        .build();
            }
        }

        static class LokiQueryRangeResponseDeserializer extends StdDeserializer<LokiQueryRangeResponse> {
            final ObjectMapper mapper;

            LokiQueryRangeResponseDeserializer(ObjectMapper mapper) {
                super(LokiQueryRangeResponse.class);
                this.mapper = mapper;
            }

            @Override
            public LokiQueryRangeResponse deserialize(JsonParser p, DeserializationContext ctxt)
                    throws IOException, JacksonException {
                JsonNode root = p.getCodec().readTree(p);
                LokiResultType resultType = Optional.ofNullable(root.get("resultType"))
                        .map(JsonNode::asText)
                        .map(LokiResultType::of)
                        .orElse(null);
                TypeFactory typeFactory = ctxt.getTypeFactory();
                JavaType javaType;
                if (LokiResultType.MATRIX == resultType) {
                    javaType = typeFactory.constructCollectionType(Collection.class, typeFactory.constructType(LokiMatrix.class));
                } else if (LokiResultType.STREAMS == resultType) {
                    javaType = typeFactory.constructCollectionType(Collection.class, typeFactory.constructType(LokiStream.class));
                } else {
                    throw new IllegalArgumentException("Unknown resultType: " + resultType);
                }
                Collection<? extends LokiResult> result = mapper.treeToValue(root.get("result"), javaType);
                LokiStats stats = mapper.treeToValue(root.get("stats"), LokiStats.class);
                return LokiQueryRangeResponse.builder()
                        .resultType(resultType)
                        .result(result)
                        .stats(stats)
                        .build();
            }
        }
    }

    @RequiredArgsConstructor
    static class LokiQueryMapEncoder implements QueryMapEncoder {
        final QueryMapEncoder queryMapEncoder;

        @Override
        public Map<String, Object> encode(Object object) {
            return remapLokiFields(object, queryMapEncoder.encode(object));
        }
    }
}
