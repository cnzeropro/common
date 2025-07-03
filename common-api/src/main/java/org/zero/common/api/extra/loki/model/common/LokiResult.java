package org.zero.common.api.extra.loki.model.common;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/27
 */
public abstract class LokiResult implements Serializable {
    public abstract Collection<? extends LokiValue> getValues();
}
