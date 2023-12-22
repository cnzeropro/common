package org.zero.common.core.util.java.codec;

import lombok.experimental.UtilityClass;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/6/20
 */
@UtilityClass
public class Base64Util {
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    /**
     * 编码
     */
    public String encode(String content) {
        return encode(content, StandardCharsets.UTF_8);
    }

    /**
     * 编码
     */
    public String encode(String content, Charset charset) {
        return ENCODER.encodeToString(content.getBytes(charset));
    }

    /**
     * 解码
     */
    public String decode(String content) {
        return decode(content, StandardCharsets.UTF_8);
    }

    /**
     * 解码
     */
    public String decode(String content, Charset charset) {
        return new String(DECODER.decode(content), charset);
    }
}
