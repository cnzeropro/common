package org.zero.common.core.util.java.io;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;

/**
 * 可以写入字节、字符、字符串、字符序列等等的输出流，通过融合各类方法，解决了传统OutputStream（字节输出流）只能写字节相关的信息和传统Writer（字符输出流）只能写字符相关的信息的问题。
 * 但也导致了其他一些问题，比如：字符默认使用UTF_8编码（也可指定），如果写入的字节不是通过指定编码而来的话，可能会出现乱码。
 * <p>
 * 多个类拷贝融合而来，包括{@link java.io.PrintWriter}、{@link javax.servlet.ServletOutputStream}、{@link java.io.OutputStreamWriter}等等。
 * <p>
 * 可以的话还是应该继承自{@link cn.hutool.core.io.FastByteArrayOutputStream}或者{@link org.springframework.util.FastByteArrayOutputStream}等实现，此处为了不依赖其他非官方文件，所以选择继承了{@link ByteArrayOutputStream}。
 *
 * @author zero
 * @since 2022/11/28
 */
public class ByteArrayOutputStreamWriter extends ByteArrayOutputStream implements Appendable {
    protected final Charset charset;

    protected Formatter formatter;

    public ByteArrayOutputStreamWriter() {
        this(StandardCharsets.UTF_8);
    }

    public ByteArrayOutputStreamWriter(Charset charset) {
        this.charset = charset;
    }

    public void write(char[] chars) {
        write(chars, 0, chars.length);
    }

    @SneakyThrows
    public void write(char[] chars, int off, int len) {
        write(StringCoding.encode(charset, chars, off, len));
    }

    public void write(String str) {
        write(str, 0, str.length());
    }

    public void write(String str, int off, int len) {
        if (Objects.isNull(str)) {
            str = "null";
        }
        char[] chars = new char[len];
        str.getChars(off, (off + len), chars, 0);
        write(chars);
    }

    @Override
    public ByteArrayOutputStreamWriter append(CharSequence csq) {
        if (Objects.isNull(csq)) {
            write("null");
        } else {
            write(csq.toString());
        }
        return this;
    }

    @Override
    public ByteArrayOutputStreamWriter append(CharSequence csq, int start, int end) {
        CharSequence cs = (csq == null ? "null" : csq);
        write(cs.subSequence(start, end).toString());
        return this;
    }

    @Override
    public ByteArrayOutputStreamWriter append(char c) {
        write(c);
        return this;
    }

    public void print(boolean b) {
        print(b ? "true" : "false");
    }

    public void print(char c) {
        write(c);
    }

    public void print(int i) {
        write(String.valueOf(i));
    }

    public void print(long l) {
        write(String.valueOf(l));
    }

    public void print(float f) {
        write(String.valueOf(f));
    }

    public void print(double d) {
        write(String.valueOf(d));
    }

    public void print(char[] chars) {
        write(chars);
    }

    public void print(String s) {
        write(s);
    }

    public void print(Object obj) {
        write(String.valueOf(obj));
    }

    public void println() {
        print("\r\n");
    }

    public synchronized void println(boolean b) {
        print(b);
        println();
    }

    public synchronized void println(char c) {
        print(c);
        println();
    }

    public synchronized void println(int i) {
        print(i);
        println();
    }


    public synchronized void println(long l) {
        print(l);
        println();
    }

    public synchronized void println(float f) {
        print(f);
        println();
    }

    public synchronized void println(double d) {
        print(d);
        println();
    }

    public synchronized void println(char[] chars) {
        print(chars);
        println();
    }

    public synchronized void println(String s) {
        print(s);
        println();
    }

    public synchronized void println(Object obj) {
        print(String.valueOf(obj));
        println();
    }

    public void printf(String format, Object... args) {
        format(format, args);
    }

    public void printf(Locale l, String format, Object... args) {
        format(l, format, args);
    }

    @Override
    public synchronized String toString() {
        return toString(charset);
    }

    public synchronized String toString(Charset charset) {
        return new String(buf, 0, count, charset);
    }

    private void format(String format, Object... args) {
        format(Locale.getDefault(), format, args);
    }

    private synchronized void format(Locale locale, String format, Object... args) {
        if (Objects.isNull(formatter) || formatter.locale() != locale) {
            formatter = new Formatter(this, locale);
        }
        formatter.format(locale, format, args);
    }
}
