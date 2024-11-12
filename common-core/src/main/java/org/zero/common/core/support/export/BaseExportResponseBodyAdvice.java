package org.zero.common.core.support.export;

import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.zero.common.data.exception.CommonException;
import org.zero.common.data.model.vo.Result;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * 基础表格数据导出 ResponseBodyAdvice
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2022/5/23
 */
public abstract class BaseExportResponseBodyAdvice implements ResponseBodyAdvice<Object>, Ordered {
    protected int order = Ordered.LOWEST_PRECEDENCE;

    protected BaseExportResponseBodyAdvice() {
    }

    protected BaseExportResponseBodyAdvice(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // ResponseEntity 不做处理
        if (body instanceof ResponseEntity) {
            return body;
        }
        // Result 适配
        Object data = body;
        if (body instanceof Result) {
            Result<?> result = (Result<?>) body;
            data = result.getData();
        }
        // null 值不做处理
        if (Objects.isNull(data)) {
            return null;
        }

        // 获取导出信息
        FileExportEntity fileExportEntity = this.export(data, returnType, selectedContentType, selectedConverterType, request, response);
        if (Objects.isNull(fileExportEntity)) {
            throw new CommonException("no file export");
        }
        // 设置响应头
        this.setResponseHeader(response.getHeaders(), fileExportEntity);
        // 响应输出
        this.respond(response.getBody(), fileExportEntity);
        // 标识请求已处理
        return null;
    }

    protected abstract FileExportEntity export(@NonNull Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType, @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response);

    /**
     * 响应输出
     * <p>
     * 使用 {@link FastByteArrayOutputStream} 倒腾一下，用于获取 ContentLength
     */
    @SneakyThrows
    protected void respond(OutputStream outputStream, FileExportEntity fileExportEntity) {
        FastByteArrayOutputStream fastByteArrayOutputStream = fileExportEntity.getFastByteArrayOutputStream();
        if (Objects.nonNull(fastByteArrayOutputStream)) {
            fastByteArrayOutputStream.writeTo(outputStream);
            fastByteArrayOutputStream.close();
        }
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 设置响应头
     */
    protected void setResponseHeader(HttpHeaders headers, FileExportEntity fileExportEntity) {
        // set content type
        MediaType mediaType = MediaType.valueOf(fileExportEntity.getContentType());
        headers.setContentType(mediaType);

        // set content disposition
        String filename = fileExportEntity.getFilename();
        String extName = fileExportEntity.getExtName();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        if (StringUtils.hasText(extName)) {
            if (!StringUtils.hasText(filename)) {
                filename = String.format("%s.%s", now, extName);
            } else if (!filename.toLowerCase().endsWith(extName)) {
                filename += extName;
            }
        } else {
            if (!StringUtils.hasText(filename)) {
                filename = now;
            }
        }
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build();
        headers.setContentDisposition(contentDisposition);

        // set cache control
        CacheControl cacheControl = CacheControl.noStore();
        headers.setCacheControl(cacheControl);

        // set content length
        headers.setContentLength(fileExportEntity.getFastByteArrayOutputStream().size());
    }

    protected static boolean isExpectedType(Object obj, Class<?> expectedType) {
        return expectedType.isInstance(obj);
    }

    protected static <T> boolean allNonNullMatch(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream()
                .filter(Objects::nonNull)
                .allMatch(predicate);
    }

    protected static <T> boolean allNonNullMatch(T[] array, Predicate<T> predicate) {
        return Arrays.stream(array)
                .filter(Objects::nonNull)
                .allMatch(predicate);
    }
}
