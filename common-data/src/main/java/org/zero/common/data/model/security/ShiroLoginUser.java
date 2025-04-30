package org.zero.common.data.model.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zero
 * @since 2021/8/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiroLoginUser implements LoginUser {
    private Serializable id;
    private String name;
    private Map<String, Object> attributes = new LinkedHashMap<>();
}
