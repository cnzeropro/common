package org.zero.common.core.support.api.deduplicate.voucher;

import org.zero.common.core.support.api.deduplicate.annotation.Deduplicate;
import org.zero.common.core.util.spring.web.context.request.javax.RequestUtil;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/3
 */
public class RequestHeaderEquivalentVoucher implements BaseEquivalentVoucher {
    public static final RequestHeaderEquivalentVoucher INSTANCE = new RequestHeaderEquivalentVoucher();

    @Override
    public Object create(Object context, Deduplicate deduplicate) {
        return RequestUtil.getHttpServletRequestOpt()
                .map(org.zero.common.core.util.javax.servlet.RequestUtil::getHeaders)
                .orElse(null);
    }
}
