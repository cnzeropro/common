package org.zero.common.api.dynamic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/27
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ApiPO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private String path;
    private String httpMethod;
    private String processor;
    private Integer status;
}
