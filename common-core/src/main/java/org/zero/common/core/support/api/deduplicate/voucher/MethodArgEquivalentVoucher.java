package org.zero.common.core.support.api.deduplicate.voucher;

import org.aspectj.lang.JoinPoint;
import org.zero.common.core.support.api.deduplicate.annotation.Deduplicate;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/3
 */
public class MethodArgEquivalentVoucher implements BaseEquivalentVoucher {
    public static final MethodArgEquivalentVoucher INSTANCE = new MethodArgEquivalentVoucher();

    @Override
    public Object create(Object context, Deduplicate deduplicate) {
        if (context instanceof JoinPoint) {
            JoinPoint joinPoint = (JoinPoint) context;
            return joinPoint.getArgs();
        }
        // HandlerMethod 不支持
        return null;
    }
}
