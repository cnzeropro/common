package org.zero.common.data.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * @author zero
 * @since 2021/8/22
 */
@Data
public class ShiroLoginUser {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String username;
}
