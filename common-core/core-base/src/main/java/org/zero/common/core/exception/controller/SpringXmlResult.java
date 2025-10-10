package org.zero.common.core.exception.controller;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/6
 */
@Data
@AllArgsConstructor(staticName = "of")
@JacksonXmlRootElement(localName = "Result")
public class SpringXmlResult {
    @JacksonXmlProperty(localName = "Timestamp")
    private Date timestamp;
    @JacksonXmlProperty(localName = "Status")
    private Integer status;
    @JacksonXmlProperty(localName = "Error")
    private String error;
    @JacksonXmlProperty(localName = "Trace")
    private String trace;
    @JacksonXmlProperty(localName = "Message")
    private String message;
    @JacksonXmlProperty(localName = "Path")
    private String path;

    public static SpringXmlResult of(Map<String, Object> result) {
        return SpringXmlResult.of((Date) result.get("timestamp"),
                (Integer) result.get("status"),
                (String) result.get("error"),
                (String) result.get("trace"),
                (String) result.get("message"),
                (String) result.get("path"));
    }
}
