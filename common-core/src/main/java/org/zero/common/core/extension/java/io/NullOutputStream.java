package org.zero.common.core.extension.java.io;

import java.io.OutputStream;

/**
 * copy from {@linkplain org.apache.commons.io.output.NullOutputStream apache commons io NullOutputStream}
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/11
 */
public class NullOutputStream extends OutputStream {
    public static final NullOutputStream INSTANCE = new NullOutputStream();

    /**
     * Does nothing - output to {@code /dev/null}.
     *
     * @param b The bytes to write
     */
    @Override
    public void write(final byte[] b) {
        // to /dev/null
    }

    /**
     * Does nothing - output to {@code /dev/null}.
     *
     * @param b   The bytes to write
     * @param off The start offset
     * @param len The number of bytes to write
     */
    @Override
    public void write(final byte[] b, final int off, final int len) {
        // to /dev/null
    }

    /**
     * Does nothing - output to {@code /dev/null}.
     *
     * @param b The byte to write
     */
    @Override
    public void write(final int b) {
        // to /dev/null
    }

    @Override
    public void flush() {
        // do nothing
    }

    @Override
    public void close() {
        // do nothing
    }
}
