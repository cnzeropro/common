package org.zero.common.core.support.api.deduplicate.voucher;

import org.aspectj.lang.JoinPoint;
import org.springframework.web.method.HandlerMethod;
import org.zero.common.core.support.api.deduplicate.annotation.Deduplicate;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/2
 */
public class DefaultEquivalentVoucher implements EquivalentVoucher {
    public static final DefaultEquivalentVoucher INSTANCE = new DefaultEquivalentVoucher();

    @Override
    public String generate(Object context, Deduplicate deduplicate) {
        if (context instanceof JoinPoint) {
            return String.format("%s|%s",
                    MethodEquivalentVoucher.INSTANCE.generate(context, deduplicate),
                    MethodArgEquivalentVoucher.INSTANCE.generate(context, deduplicate));
        }
        if (context instanceof HandlerMethod) {
            return String.format("%s|%s|%s|%s",
                    RequestMethodEquivalentVoucher.INSTANCE.generate(context, deduplicate),
                    RequestUriEquivalentVoucher.INSTANCE.generate(context, deduplicate),
                    RequestParamEquivalentVoucher.INSTANCE.generate(context, deduplicate),
                    RequestBodyEquivalentVoucher.INSTANCE.generate(context, deduplicate));
        }
        return null;
    }
}
