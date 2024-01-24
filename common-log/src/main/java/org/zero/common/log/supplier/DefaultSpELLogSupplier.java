package org.zero.common.log.supplier;

import lombok.extern.slf4j.Slf4j;
import org.zero.common.core.util.spring.SpELUtil;

/**
 * @author zero
 * @date 2022/1/3
 */
@Slf4j
public class DefaultSpELLogSupplier implements LogSupplier {
    public static final LogSupplier INSTANCE = new DefaultSpELLogSupplier();

    @Override
    public String getMessage(LogContext context) {
        String msg = context.getMessageTemplate();
        try {
            msg = SpELUtil.evaluate(context.getMethod(), context.getParams(), msg, String.class);
        } catch (Exception e) {
            log.warn(String.format("@AutoLog parse SpEL[%s] error", msg), e);
        }
        return msg;
    }
}
