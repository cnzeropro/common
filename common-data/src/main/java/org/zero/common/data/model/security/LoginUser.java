package org.zero.common.data.model.security;

import java.io.Serializable;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/14
 */
public interface LoginUser extends Principal, Serializable {
    void setId(Serializable id);

    Serializable getId();

    void setName(String name);

    String getName();

    void setAttributes(Map<String, Object> attributes);

    Map<String, Object> getAttributes();

    default Optional<Object> getAttributeOpt(String key) {
        Map<String, Object> attributes = this.getAttributes();
        if (attributes == null) {
            return Optional.empty();
        }
        Object attribute = attributes.get(key);
        return Optional.ofNullable(attribute);
    }

    default <T> Optional<T> getAttributeOpt(String key, Class<T> type) {
        return this.getAttributeOpt(key).map(type::cast);
    }
}
