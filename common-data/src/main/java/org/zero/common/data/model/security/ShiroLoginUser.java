package org.zero.common.data.model.security;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zero
 * @since 2021/8/22
 */
@Data
public class ShiroLoginUser {
    private final Serializable id;
    private final String name;
}
