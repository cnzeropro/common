package org.zero.common.core.support.api.crypto;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/25
 */
public enum StringMode {
    HEX {
        @Override
        public String toString(byte[] bytes) {
            return HexUtil.encodeHexStr(bytes);
        }

        @Override
        public byte[] toBytes(String string) {
            return HexUtil.decodeHex(string);
        }
    },
    BASE64 {
        @Override
        public String toString(byte[] bytes) {
            return Base64.encode(bytes);
        }

        @Override
        public byte[] toBytes(String string) {
            return Base64.decode(string);
        }
    },
    UTF8 {
        @Override
        public String toString(byte[] bytes) {
            return new String(bytes, StandardCharsets.UTF_8);
        }

        @Override
        public byte[] toBytes(String string) {
            return string.getBytes(StandardCharsets.UTF_8);
        }
    };

    public abstract String toString(byte[] bytes);

    public abstract byte[] toBytes(String string);
}
