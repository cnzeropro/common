package org.zero.common.core.support.api.deduplication.voucher;

import org.zero.common.core.support.api.deduplication.annotation.Deduplication;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/2
 */
public class DefaultEquivalentVoucher implements EquivalentVoucher {
    public static final DefaultEquivalentVoucher INSTANCE = new DefaultEquivalentVoucher();

    @Override
    public String generate(Object context, Deduplication deduplication) {

    }
}
