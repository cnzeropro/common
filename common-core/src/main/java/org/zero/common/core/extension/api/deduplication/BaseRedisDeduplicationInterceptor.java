package org.zero.common.core.extension.api.deduplication;

import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;
import java.util.stream.Collectors;

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
                .map(this::md5)
                .collect(Collectors.joining("-"));
    }

    @SneakyThrows
    private Object getEquivalentVoucher(HttpServletRequest request, EquivalentVoucherType equivalentVoucherType) {
        switch (equivalentVoucherType) {
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
    private String md5(Object input) {
        byte[] bytes;
        if (input instanceof byte[]) {
            bytes = (byte[]) input;
        } else if (input instanceof CharSequence) {
            CharSequence charSequence = (CharSequence) input;
            bytes = charSequence.toString().getBytes();
        } else if (input instanceof Reader) {
            Reader reader = (Reader) input;
            bytes = FileCopyUtils.copyToString(reader).getBytes();
        } else if (input instanceof InputStream) {
            InputStream inputStream = (InputStream) input;
            bytes = StreamUtils.copyToByteArray(inputStream);
        } else if (ObjectUtils.isArray(input)) {
            Object[] array = ObjectUtils.toObjectArray(input);
            bytes = Arrays.toString(array).getBytes();
        } else {
            bytes = Objects.toString(input).getBytes();
        }
        return DigestUtils.md5DigestAsHex(bytes);
    }
}
