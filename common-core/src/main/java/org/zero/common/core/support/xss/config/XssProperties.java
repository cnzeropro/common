package org.zero.common.core.support.xss.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.zero.common.core.support.xss.processor.UnitType;
import org.zero.common.core.support.xss.processor.XssMode;
import org.zero.common.core.support.xss.processor.XssProcessor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/15
 */
@ConfigurationProperties("sys.web.xss")
@Setter
@Getter
public class XssProperties {
    /**
     * 是否启用。
     */
    private boolean enabled = false;
    /**
     * XSS 处理器类。
     *
     * @see XssProcessor
     */
    private Class<? extends XssProcessor> processorClass = XssMode.FILTER.getClass();
    /**
     * 忽略的 URI，支持 Ant-style 路径模式。
     *
     * @see org.springframework.util.AntPathMatcher
     */
    private String[] excludeUris = new String[]{};
    /**
     * 需要处理的 URI。
     * <p>
     * key：URI，支持 Ant-style 路径模式。<br>
     * value：需要处理的模块类型。
     * <p>
     * 默认处理所有请求中的模块类型为 {@link UnitType#PARAM}、{@link UnitType#QUERY_STRING}、{@link UnitType#BODY_BYTE}、{@link UnitType#BODY_CHAR}。<br>
     * 如需更改建议重新配置 {@code /**}
     *
     * @see UnitType
     */
    private Map<String, UnitType[]> processableUriTypes = new LinkedHashMap<>();

    public Map<String, UnitType[]> getProcessableUriTypes() {
        processableUriTypes.putIfAbsent("/**", new UnitType[]{UnitType.PARAM, UnitType.QUERY_STRING, UnitType.BODY_BYTE, UnitType.BODY_CHAR});
        return processableUriTypes;
    }
}
