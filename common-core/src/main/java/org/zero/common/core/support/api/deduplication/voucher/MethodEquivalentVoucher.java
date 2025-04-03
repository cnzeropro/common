package org.zero.common.core.support.api.deduplication.voucher;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.method.HandlerMethod;
import org.zero.common.core.support.api.deduplication.annotation.Deduplication;

import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/3
 */
public class MethodEquivalentVoucher implements BaseEquivalentVoucher {
    public static final MethodEquivalentVoucher INSTANCE = new MethodEquivalentVoucher();

    @Override
    public Object create(Object context, Deduplication deduplication) {
        if (context instanceof JoinPoint) {
            JoinPoint joinPoint = (JoinPoint) context;
            return Optional.ofNullable(joinPoint.getSignature())
                    .filter(MethodSignature.class::isInstance)
                    .map(MethodSignature.class::cast)
                    .map(MethodSignature::getMethod)
                    .orElse(null);
        }
        if (context instanceof HandlerMethod) {
            return ((HandlerMethod) context).getMethod();
        }
        return null;
    }
}
