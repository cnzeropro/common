package org.zero.common.core.support.spring;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/6
 */
@Setter
@Getter
@Accessors(chain = true)
public class DownloadByteContext {
    private final byte[] content;
    private long contentLength;
    private String contentName;
    private String contentType = "application/octet-stream";
    private String contentSign;
    private int httpStatus = 200;
    private Map<String, String> httpHeaders = new HashMap<>();

    public static DownloadByteContext create(byte[] content) {
        return new DownloadByteContext(content);
    }

    public DownloadByteContext addHttpHeader(String name, String value) {
        httpHeaders.put(name, value);
        return this;
    }

    private DownloadByteContext(byte[] content) {
        this.content = content;
        this.contentLength = content.length;
    }
}
