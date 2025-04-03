package org.zero.common.core.support.api.deduplication.voucher;

import org.zero.common.core.support.api.deduplication.annotation.Deduplication;
import org.zero.common.core.util.spring.web.RequestUtil;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/3
 */
public class RequestBodyEquivalentVoucher implements BaseEquivalentVoucher {
    public static final RequestBodyEquivalentVoucher INSTANCE = new RequestBodyEquivalentVoucher();

    @Override
    public Object create(Object context, Deduplication deduplication) {
        return RequestUtil.getHttpServletRequestOpt()
                .map(org.zero.common.core.util.javax.servlet.RequestUtil::getHeaders)
                .orElse(null);
    }
}
