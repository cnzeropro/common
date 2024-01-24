package org.zero.common.log.supplier;

/**
 * @author yufa.wang (yufa.wang@ronganchina.com)
 * @since 2023/9/1
 */
public class DefaultLogSupplier implements LogSupplier {
    public static final LogSupplier INSTANCE = new DefaultLogSupplier();

    @Override
    public String getMessage(LogContext context) {
        return context.getMessageTemplate();
    }
}
