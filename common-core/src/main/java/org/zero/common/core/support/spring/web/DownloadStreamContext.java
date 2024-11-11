package org.zero.common.core.support.spring.web;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/6
 */
@Setter
@Getter
@Accessors(chain = true)
public class DownloadStreamContext {
    /**
     * 默认缓冲区大小：1M
     */
    public static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;

    private final InputStream content;
    /**
     * 流并不总是知道数据长度，因此默认：-1（未知）
     */
    private long contentLength = -1;
    private int bufferSize = DEFAULT_BUFFER_SIZE;
    private String contentName;
    private String contentType = "application/octet-stream";
    private int httpStatus = 200;
    private Map<String, String> httpHeaders = new HashMap<>();

    public static DownloadStreamContext create(InputStream content) {
        return new DownloadStreamContext(content);
    }

    public static DownloadStreamContext create(InputStream content, long contentLength) {
        return new DownloadStreamContext(content, contentLength);
    }

    public DownloadStreamContext addHttpHeader(String name, String value) {
        httpHeaders.put(name, value);
        return this;
    }

    private DownloadStreamContext(InputStream content) {
        this.content = content;
    }

    /**
     * 未使用 {@link java.io.InputStream#available} 在于该方法的行为依赖于具体的输入流实现，并且它的返回值并不能保证是流的总长度
     */
    private DownloadStreamContext(InputStream content, long contentLength) {
        this.content = content;
        this.contentLength = contentLength;
    }
}
