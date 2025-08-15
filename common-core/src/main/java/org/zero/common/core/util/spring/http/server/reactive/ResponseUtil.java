package org.zero.common.core.util.spring.http.server.reactive;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/16
 */
@Slf4j
@UtilityClass
public class ResponseUtil {
    public static Mono<Void> writeOkJson(ServerHttpResponse response, byte[] jsonBytes) {
        return writeJson(response, jsonBytes, HttpStatus.OK);
    }

    public static Mono<Void> writeErrorJson(ServerHttpResponse response, byte[] jsonBytes) {
        return writeJson(response, jsonBytes, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static Mono<Void> writeJson(ServerHttpResponse response, byte[] jsonBytes, HttpStatus httpStatus) {
        return write(response, jsonBytes, httpStatus, MediaType.APPLICATION_JSON);
    }

    public static Mono<Void> write(ServerHttpResponse response, byte[] bytes, HttpStatus httpStatus, MediaType mediaType) {
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(mediaType);
        DataBufferFactory bufferFactory = response.bufferFactory();
        DataBuffer buffer = bufferFactory.wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}
