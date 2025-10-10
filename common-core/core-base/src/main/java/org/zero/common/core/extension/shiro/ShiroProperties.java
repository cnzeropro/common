package org.zero.common.core.extension.shiro;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/23
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "system.artifact.shiro")
public class ShiroProperties {
    private Map<String, String> pathDefinition = new LinkedHashMap<>();
}
