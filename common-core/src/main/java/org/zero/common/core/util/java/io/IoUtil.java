package org.zero.common.core.util.java.io;

import lombok.SneakyThrows;
import org.zero.common.core.extension.java.io.NullOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/11
 */
public class IoUtil {
    /**
     * 默认的缓冲区大小
     *
     * @see java.io.BufferedInputStream#DEFAULT_BUFFER_SIZE
     * @see java.io.BufferedReader#DEFAULT_CHAR_BUFFER_SIZE
     */
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final int EOF = -1;

    /**
     * 将输入流中的内容拷贝到输出流中
     *
     * @param in         输入流
     * @param out        输出流
     * @param bufferSize 缓冲区大小
     * @param closeIn    是否关闭输入流
     * @return 拷贝的字节数
     */
    @SneakyThrows
    public static long copy(final InputStream in, final OutputStream out, int bufferSize, boolean closeIn) {
        long count = 0L;
        byte[] buffer = new byte[bufferSize];
        int read;
        while ((read = in.read(buffer)) != EOF) {
            out.write(buffer, 0, read);
            count += read;
        }
        if (closeIn) {
            in.close();
        }
        return count;
    }

    /**
     * 将输入流中的内容拷贝到输出流中，默认不关闭输入流
     *
     * @param in         输入流
     * @param out        输出流
     * @param bufferSize 缓冲区大小
     * @return 拷贝的字节数
     */
    public static long copy(final InputStream in, final OutputStream out, int bufferSize) {
        return copy(in, out, bufferSize, false);
    }

    /**
     * 使用默认缓冲区大小{@link #DEFAULT_BUFFER_SIZE}将输入流中的内容拷贝到输出流中
     *
     * @param in      输入流
     * @param out     输出流
     * @param closeIn 是否关闭输入流
     * @return 拷贝的字节数
     */
    public static long copy(final InputStream in, final OutputStream out, boolean closeIn) {
        return copy(in, out, DEFAULT_BUFFER_SIZE, closeIn);
    }

    /**
     * 使用默认缓冲区大小{@link #DEFAULT_BUFFER_SIZE}将输入流中的内容拷贝到输出流中，默认不关闭输入流
     *
     * @param in  输入流
     * @param out 输出流
     * @return 拷贝的字节数
     */
    public static long copy(final InputStream in, final OutputStream out) {
        return copy(in, out, DEFAULT_BUFFER_SIZE);
    }

    @SneakyThrows
    public static long copy(final Reader reader, final Writer writer, int bufferSize, boolean closeIn) {
        long count = 0L;
        char[] buffer = new char[bufferSize];
        int read;
        while ((read = reader.read(buffer)) != EOF) {
            writer.write(buffer, 0, read);
            count += read;
        }
        if (closeIn) {
            reader.close();
        }
        return count;
    }

    public static long copy(final Reader reader, final Writer writer, int bufferSize) {
        return copy(reader, writer, bufferSize, false);
    }

    public static long copy(final Reader reader, final Writer writer, boolean closeIn) {
        return copy(reader, writer, DEFAULT_BUFFER_SIZE, closeIn);
    }

    public static long copy(final Reader reader, final Writer writer) {
        return copy(reader, writer, DEFAULT_BUFFER_SIZE);
    }

    public static byte[] readAll(InputStream in) {
        return readAll(in, false);
    }

    @SneakyThrows
    public static byte[] readAll(InputStream in, boolean close) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            copy(in, out, close);
            return out.toByteArray();
        }
    }

    @SneakyThrows
    public static char[] readAll(Reader reader, boolean close) {
        try (CharArrayWriter writer = new CharArrayWriter()) {
            copy(reader, writer, close);
            return writer.toCharArray();
        }
    }

    /**
     * 精准地获取输入流的大小
     * <p>
     * 注意：
     * 1、能正确获取流总字节数的大前提是该流未被提前读取过
     * 2、经此方法处理的输入流，再次使用可能会造成数据丢失
     *
     * @param in      输入流
     * @param closeIn 是否关闭输入流
     * @return 输入流的大小（总字节数）
     */
    public static long getStreamSizExact(InputStream in, boolean closeIn) {
        return copy(in, NullOutputStream.INSTANCE, closeIn);
    }

    /**
     * 精准地获取输入流的大小，并默认关闭
     * 注意：能正确获取流总字节数的大前提是该流未被提前读取过
     *
     * @param in 输入流
     * @return 输入流的大小（总字节数）
     */
    public static long getStreamSizExact(InputStream in) {
        return getStreamSizExact(in, true);
    }

    /**
     * 初步获取输入流的大小，默认不关闭
     * <p>
     * 注意：
     * 1、能正确获取流总字节数的大前提是该流未被提前读取过
     * 2、此方法也有一定弊端，即传入的输入流不一定满足可以统计总字节数的要求，比如：网络流、Mark Not Supported 的流等等
     *
     * @param in 输入流
     * @return 输入流的大小（总字节数）
     */
    @SneakyThrows
    private long getStreamSizeAvailable(InputStream in) {
        // 使用 InputStream#available() 方法获取流中总字节数并不总是准确，与 available 方法具体实现有关，特别是网络流
        // 但文件流可使用 available() 方法
        if (in instanceof FileInputStream) {
            return in.available();
        }
        // 读取流来统计总字节数，又会出现数据丢失的问题，与流的具体实现有关，特别是 Mark not Support 的流
        if (!in.markSupported()) {
            throw new IOException("Mark not supported");
        }
        // 注意，超出 2GB（实际为 Integer.MAX_VALUE = 2^31-1）的流可能会造成数据丢失，与 mark 和 reset 方法具体实现有关
        in.mark(Integer.MAX_VALUE);
        try {
            return copy(in, NullOutputStream.INSTANCE);
        } finally {
            // 重置流
            in.reset();
        }
    }
}
