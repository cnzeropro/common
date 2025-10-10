package org.zero.common.core.support.export.csv;

import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/15
 */
@Getter
public enum ByteOrderMark {
    /**
     * UTF_8
     */
    UTF_8(StandardCharsets.UTF_8.name(), 0xEF, 0xBB, 0xBF),
    /**
     * UTF_16BE
     */
    UTF_16BE(StandardCharsets.UTF_16BE.name(), 0xFE, 0xFF),
    /**
     * UTF_16LE
     */
    UTF_16LE(StandardCharsets.UTF_16LE.name(), 0xFF, 0xFE),
    /**
     * UTF_32BE
     */
    UTF_32BE("UTF-32BE", 0x00, 0x00, 0xFE, 0xFF),
    /**
     * UTF_32LE
     */
    UTF_32LE("UTF-32LE", 0xFF, 0xFE, 0x00, 0x00),
    ;

    public static final char UTF_BOM = '\uFEFF';

    public static ByteOrderMark query(String charsetName) {
        return queryOpt(charsetName).orElse(null);
    }

    public static Optional<ByteOrderMark> queryOpt(String charsetName) {
        for (ByteOrderMark byteOrderMark : values()) {
            if (byteOrderMark.charsetName.equals(charsetName)) {
                return Optional.of(byteOrderMark);
            }
        }
        return Optional.empty();
    }

    private final String charsetName;
    private final int[] bytes;

    ByteOrderMark(String charsetName, int... bytes) {
        this.charsetName = charsetName;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        final byte[] copy = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            copy[i] = (byte) bytes[i];
        }
        return copy;
    }

    @SneakyThrows
    public String getBom() {
        return new String(this.getBytes(), charsetName);
    }
}
