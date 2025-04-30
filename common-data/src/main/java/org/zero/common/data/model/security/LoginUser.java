package org.zero.common.data.model.security;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/14
 */
public interface LoginUser extends Serializable {
    void setId(Serializable id);

    Serializable getId();

    void setName(String name);

    String getName();

    Map<String, Object> getAttributes();

    void setAttributes(Map<String, Object> attributes);
}
