package org.zero.common.core.support.api.deduplicate.voucher;

import org.zero.common.core.support.api.deduplicate.annotation.Deduplicate;
import org.zero.common.core.util.spring.web.context.request.javax.RequestUtil;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/3
 */
public class RequestBodyEquivalentVoucher implements BaseEquivalentVoucher {
    public static final RequestBodyEquivalentVoucher INSTANCE = new RequestBodyEquivalentVoucher();

    @Override
    public Object create(Object context, Deduplicate deduplicate) {
        return RequestUtil.getHttpServletRequestOpt()
                .map(request -> {
                    try {
                        return org.zero.common.core.util.javax.servlet.RequestUtil.getByteBody(request);
                    } catch (IllegalStateException ignored) {
                        return org.zero.common.core.util.javax.servlet.RequestUtil.getCodePointBody(request);
                    }
                })
                .orElse(null);
    }
}
