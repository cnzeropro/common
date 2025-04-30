package org.zero.common.core.extension.java.io;

import org.jetbrains.annotations.NotNull;

import java.io.Writer;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/27
 */
public class NullWriter extends Writer {
    public static final NullWriter INSTANCE = new NullWriter();

    @Override
    public void write(@NotNull char[] cbuf, int off, int len) {
        // to /dev/null
    }

    @Override
    public Writer append(char c) {
        // to /dev/null
        return this;
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) {
        // to /dev/null
        return this;
    }

    @Override
    public Writer append(CharSequence csq) {
        // to /dev/null
        return this;
    }

    @Override
    public void write(@NotNull String str, int off, int len) {
        // to /dev/null
    }

    @Override
    public void write(@NotNull String str) {
        // to /dev/null
    }

    @Override
    public void write(@NotNull char[] cbuf) {
        // to /dev/null
    }

    @Override
    public void write(int c) {
        // to /dev/null
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public void flush() {
        // do nothing
    }
}
