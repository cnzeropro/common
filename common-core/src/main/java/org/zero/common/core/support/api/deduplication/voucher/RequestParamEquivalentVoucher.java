package org.zero.common.core.support.api.deduplication.voucher;

import org.zero.common.core.support.api.deduplication.annotation.Deduplication;
import org.zero.common.core.util.spring.web.RequestUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/3
 */
public class RequestParamEquivalentVoucher implements BaseEquivalentVoucher {
    public static final RequestParamEquivalentVoucher INSTANCE = new RequestParamEquivalentVoucher();

    @Override
    public Object create(Object context, Deduplication deduplication) {
        return RequestUtil.getHttpServletRequestOpt()
                .map(HttpServletRequest::getParameterMap)
                .orElse(null);
    }
}
