package org.zero.common.api.extra.loki;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;
import org.springframework.util.TypeUtils;
import org.zero.common.api.extra.loki.constant.ResultType;
import org.zero.common.api.extra.loki.model.common.LokiMatrix;
import org.zero.common.api.extra.loki.model.common.LokiResult;
import org.zero.common.api.extra.loki.model.common.LokiStats;
import org.zero.common.api.extra.loki.model.common.LokiStream;
import org.zero.common.api.extra.loki.model.common.LokiVector;
import org.zero.common.api.extra.loki.model.request.LokiDeleteRequest;
import org.zero.common.api.extra.loki.model.request.LokiIngesterShutdownRequest;
import org.zero.common.api.extra.loki.model.request.LokiLogLevelRequest;
import org.zero.common.api.extra.loki.model.response.LokiQueryRangeResponse;
import org.zero.common.api.extra.loki.model.response.LokiQueryResponse;
import org.zero.common.api.extra.loki.model.response.LokiResponse;
import org.zero.common.api.extra.loki.model.response.LokiSeriesResponse;
import org.zero.common.core.extension.feign.AbstractFeignConfig;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/25
 */
@RequiredArgsConstructor
public class LokiFeignConfig extends AbstractFeignConfig {
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

    @RequiredArgsConstructor
    static class LokiFeignEncoder implements Encoder {
        final Encoder encoder;
        final ObjectMapper objectMapper;

        @Override
        public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
            if (TypeUtils.isAssignable(LokiLogLevelRequest.class, bodyType)) {
                Map<String, Object> map = objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {
                });
                if (map.containsKey("logLevel")) {
                    map.put("log_level", map.remove("logLevel"));
                }
                encoder.encode(map, MAP_STRING_WILDCARD, template);
                return;
            }
            encoder.encode(object, bodyType, template);
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
            this.objectMapper = objectMapper.copy()
                    .addMixIn(LokiSeriesResponse.class, LokiSeriesResponseMixIn.class)
                    .registerModule(module);
        }

        @Override
        public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
            if (TypeUtils
                    .isAssignable(ResolvableType
                            .forClassWithGenerics(LokiResponse.class,
                                    ResolvableType.forClassWithGenerics(Collection.class, LokiSeriesResponse.class))
                            .getType(), type)
                    ||
                    TypeUtils.isAssignable(
                            ResolvableType.forClassWithGenerics(LokiResponse.class, LokiQueryResponse.class).getType(),
                            type)
                    ||
                    TypeUtils.isAssignable(ResolvableType
                            .forClassWithGenerics(LokiResponse.class, LokiQueryRangeResponse.class).getType(), type)) {
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
                ResultType resultType = Optional.ofNullable(root.get("resultType"))
                        .map(JsonNode::asText)
                        .map(ResultType::of)
                        .orElse(null);
                // 根据 resultType 构造 result 字段对应的 JavaType
                TypeFactory typeFactory = ctxt.getTypeFactory();
                JavaType javaType;
                if (ResultType.VECTOR == resultType) {
                    javaType = typeFactory.constructCollectionType(Collection.class,
                            typeFactory.constructType(LokiVector.class));
                } else if (ResultType.STREAMS == resultType) {
                    javaType = typeFactory.constructCollectionType(Collection.class,
                            typeFactory.constructType(LokiStream.class));
                } else {
                    // never
                    throw new IllegalArgumentException("Unknown resultType: " + resultType);
                }
                // 反序列化 result 字段
                Collection<? extends LokiResult> result = mapper.treeToValue(root.get("result"), javaType);
                // 反序列化 stats 字段
                LokiStats stats = mapper.treeToValue(root.get("stats"), LokiStats.class);
                // 构造 LokiQueryRangeResponse 对象
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
                ResultType resultType = Optional.ofNullable(root.get("resultType"))
                        .map(JsonNode::asText)
                        .map(ResultType::of)
                        .orElse(null);
                // 根据 resultType 构造 result 字段对应的 JavaType
                TypeFactory typeFactory = ctxt.getTypeFactory();
                JavaType javaType;
                if (ResultType.MATRIX == resultType) {
                    javaType = typeFactory.constructCollectionType(Collection.class,
                            typeFactory.constructType(LokiMatrix.class));
                } else if (ResultType.STREAMS == resultType) {
                    javaType = typeFactory.constructCollectionType(Collection.class,
                            typeFactory.constructType(LokiStream.class));
                } else {
                    // never
                    throw new IllegalArgumentException("Unknown resultType: " + resultType);
                }
                // 反序列化 result 字段
                Collection<? extends LokiResult> result = mapper.treeToValue(root.get("result"), javaType);
                // 反序列化 stats 字段
                LokiStats stats = mapper.treeToValue(root.get("stats"), LokiStats.class);
                // 构造 LokiQueryRangeResponse 对象
                return LokiQueryRangeResponse.builder()
                        .resultType(resultType)
                        .result(result)
                        .stats(stats)
                        .build();
            }
        }

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        abstract static class LokiSeriesResponseMixIn {
        }
    }

    @RequiredArgsConstructor
    static class LokiQueryMapEncoder implements QueryMapEncoder {
        final QueryMapEncoder queryMapEncoder;

        @Override
        public Map<String, Object> encode(Object object) {
            Map<String, Object> map = queryMapEncoder.encode(object);
            // 不使用 @feign.Param 注解，此处单独处理，让实体和逻辑层解耦
            if (object instanceof LokiIngesterShutdownRequest) {
                if (map.containsKey("deleteRingTokens")) {
                    map.put("delete_ring_tokens", map.remove("deleteRingTokens"));
                }
            }
            if (object instanceof LokiDeleteRequest) {
                if (map.containsKey("maxInterval")) {
                    map.put("max_interval", map.remove("maxInterval"));
                }
            }
            return map;
        }
    }
}
