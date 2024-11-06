package org.zero.common.core.support.spring;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.FastByteArrayOutputStream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/6
 */
@Setter
@Getter
@Accessors(chain = true)
@RequiredArgsConstructor
public class DownloadStreamContext {
    private final FastByteArrayOutputStream content;
    private String contentName;
    private long contentLength;
    private String contentType;
    private String sign;
}
