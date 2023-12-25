package org.zero.common.data.enumeration;

import java.io.Serializable;

/**
 * @author Zero
 * @since 2021/8/24
 */
public interface BaseSysError extends Serializable {
    String getCode();

    String getMessage();
}
