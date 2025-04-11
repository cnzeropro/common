package org.zero.common.core.support.api.deduplicate.voucher;

import org.zero.common.core.support.api.deduplicate.annotation.Deduplicate;
import org.zero.common.core.util.spring.web.RequestUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/3
 */
public class RequestMethodEquivalentVoucher implements BaseEquivalentVoucher {
    public static final RequestMethodEquivalentVoucher INSTANCE = new RequestMethodEquivalentVoucher();

    @Override
    public Object create(Object context, Deduplicate deduplicate) {
        return RequestUtil.getHttpServletRequestOpt()
                .map(HttpServletRequest::getMethod)
                .orElse(null);
    }
}
