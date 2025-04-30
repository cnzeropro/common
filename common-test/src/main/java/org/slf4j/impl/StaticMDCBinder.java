package org.slf4j.impl;

import org.slf4j.spi.MDCAdapter;
import org.zero.common.core.extension.slf4j.spi.CustomMDCAdapter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/17
 */
public class StaticMDCBinder {
    /**
     * The unique instance of this class.
     */
    public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

    private StaticMDCBinder() {
    }

    public static StaticMDCBinder getSingleton() {
        return SINGLETON;
    }

    /**
     * Currently this method always returns an instance of
     * {@link StaticMDCBinder}.
     */
    public MDCAdapter getMDCA() {
        return new CustomMDCAdapter();
    }

    public String getMDCAdapterClassStr() {
        return CustomMDCAdapter.class.getName();
    }
}
