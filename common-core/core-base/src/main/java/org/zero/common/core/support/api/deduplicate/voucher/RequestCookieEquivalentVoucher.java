package org.zero.common.core.support.api.deduplicate.voucher;

import org.zero.common.core.support.api.deduplicate.annotation.Deduplicate;
import org.zero.common.core.util.spring.web.context.request.javax.RequestUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/3
 */
public class RequestCookieEquivalentVoucher implements BaseEquivalentVoucher {
    public static final RequestCookieEquivalentVoucher INSTANCE = new RequestCookieEquivalentVoucher();

    @Override
    public Object create(Object context, Deduplicate deduplicate) {
        return RequestUtil.getHttpServletRequestOpt()
                .map(HttpServletRequest::getCookies)
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .collect(Collectors.toMap(Cookie::getName, Cookie::getValue, (oldVal, newVal) -> newVal, LinkedHashMap::new));
    }
}
