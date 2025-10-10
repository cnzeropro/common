package org.zero.common.core.extension.jackson;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.TimeZone;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/8
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "sys.json")
public class JsonJavaTimeProperties {
    /**
     * 日期时间格式
     */
    @Value("${spring.jackson.date-format:#{null}}")
    private String datetimeFormat = "yyyy-MM-dd HH:mm:ss.SSS";
    /**
     * 日期格式
     */
    private String dateFormat = "yyyy-MM-dd";
    /**
     * 时间格式
     */
    private String timeFormat = "HH:mm:ss.SSS";
    /**
     * 时区
     * <p>
     * <b>暂不使用</b>
     */
    @Value("${spring.jackson.time-zone:#{null}}")
    private TimeZone timeZone = TimeZone.getDefault();
}
