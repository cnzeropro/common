package org.zero.common.core.support.api.deduplication.interceptor;

import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.zero.common.core.support.api.deduplication.annotation.Deduplication;
import org.zero.common.core.support.api.deduplication.voucher.EquivalentVoucherType;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/23
 */

public abstract class BaseRedisDeduplicationInterceptor extends BaseDeduplicationInterceptor {
    /**
     * 缓存 key 的前缀
     */
    public static final String KEY_PREFIX = "sys:api:deduplication";

    @Override
    protected String getDefaultKey(HttpServletRequest request, Deduplication deduplication) {
        String mark = this.getMark(request);
        String equivalentVoucher = this.getEquivalentVoucher(request, deduplication);
        if (StringUtils.hasText(mark)) {
            return String.format("%s:%s:%s", KEY_PREFIX, mark, equivalentVoucher);
        }
        return String.format("%s:%s", KEY_PREFIX, equivalentVoucher);
    }

    /**
     * 获取防抖标识
     * <p>
     * 建议重写，可返回 token、用户名、客户端 ip 等等
     */
    protected String getMark(HttpServletRequest request) {
        return null;
    }

    /**
     * 获取防抖等效凭证
     */
    protected String getEquivalentVoucher(HttpServletRequest request, Deduplication deduplication) {
        EquivalentVoucherType[] equivalentVoucherTypes = deduplication.equivalentVoucherTypes();
        if (ObjectUtils.isEmpty(equivalentVoucherTypes)) {
            return null;
        }
        if (Arrays.stream(equivalentVoucherTypes).anyMatch(equivalentVoucherType -> equivalentVoucherType == EquivalentVoucherType.NONE)) {
            return null;
        }
        return Arrays.stream(equivalentVoucherTypes)
                .map(equivalentVoucherType -> this.getEquivalentVoucher(request, equivalentVoucherType))
                .map(this::serialize)
                .map(DigestUtils::md5DigestAsHex)
                .collect(Collectors.joining("-"));
    }

    @SneakyThrows
    private Object getEquivalentVoucher(HttpServletRequest request, EquivalentVoucherType equivalentVoucherType) {
        switch (equivalentVoucherType) {
            case AUTO:
                return Stream.of(EquivalentVoucherType.REQUEST_METHOD, EquivalentVoucherType.REQUEST_URI, EquivalentVoucherType.REQUEST_PARAMS, EquivalentVoucherType.REQUEST_BODY)
                        .map(type -> this.getEquivalentVoucher(request, type))
                        .collect(Collectors.toList());
            case CUSTOM:
                return this.getCustomEquivalentVoucher(request);
            case REQUEST_METHOD:
                return request.getMethod();
            case REQUEST_URI:
                return request.getRequestURI();
            case REQUEST_PARAMS:
                return request.getParameterMap();
            case REQUEST_BODY:
                try {
                    return request.getInputStream();
                } catch (IllegalStateException ignored) {
                    return request.getReader();
                }
            case REQUEST_HEADERS:
                Enumeration<String> headerNames = request.getHeaderNames();
                HttpHeaders headers = new HttpHeaders();
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    headers.add(headerName, request.getHeader(headerName));
                }
                return headers;
            case REQUEST_COOKIES:
                Cookie[] cookies = request.getCookies();
                return Arrays.stream(cookies)
                        .collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
            default:
                return null;
        }
    }

    /**
     * 获取自定义防抖等效凭证
     * <p>
     * 如果指定 {@link EquivalentVoucherType} 为 CUSTOM，需要重写
     */
    protected Object getCustomEquivalentVoucher(HttpServletRequest request) {
        return null;
    }

    @SneakyThrows
    private byte[] serialize(Object input) {
        try {
            return SerializationUtils.serialize(input);
        } catch (Exception ignored) {
            if (input instanceof Reader) {
                Reader reader = (Reader) input;
                return FileCopyUtils.copyToString(reader).getBytes();
            }
            if (input instanceof InputStream) {
                InputStream inputStream = (InputStream) input;
                return StreamUtils.copyToByteArray(inputStream);
            }
            if (ObjectUtils.isArray(input)) {
                Object[] array = ObjectUtils.toObjectArray(input);
                return Arrays.stream(array)
                        .map(this::serialize)
                        .reduce(this::append)
                        .orElse(new byte[0]);
            }
            if (input instanceof Collection) {
                Collection<?> collection = (Collection<?>) input;
                return collection.stream()
                        .map(this::serialize)
                        .reduce(this::append)
                        .orElse(new byte[0]);
            }
            if (input instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) input;
                return map.entrySet().stream()
                        .map(entry -> append(serialize(entry.getKey()), serialize(entry.getValue())))
                        .reduce(this::append)
                        .orElse(new byte[0]);
            }
            return Objects.toString(input).getBytes();
        }
    }

    private byte[] append(byte[] bytes1, byte[] bytes2) {
        byte[] bytes = (byte[]) Array.newInstance(byte.class, bytes1.length + bytes2.length);
        System.arraycopy(bytes1, 0, bytes, 0, bytes1.length);
        System.arraycopy(bytes2, 0, bytes, bytes1.length, bytes2.length);
        return bytes;
    }
}
