package org.zero.common.log.supplier;

/**
 * @author zero
 * @date 2022/1/3
 */
@FunctionalInterface
public interface LogSupplier {
    String getMessage(LogContext context);
}
