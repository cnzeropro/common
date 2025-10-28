package org.zero.common.core.extension.java.io;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;

/**
 * 可以写入字节、字符、字符串、字符序列等等的输出流。
 * 通过融合各类方法，解决了传统 OutputStream（字节输出流）只能写字节相关信息以及传统 Writer（字符输出流）只能写字符相关信息的问题。
 * <p>
 * 但该类也导致了其他一些问题，比如：字符默认使用 UTF_8 编码（也可指定），如果写入的字节不是通过指定编码而来的话，可能会出现乱码。
 * <p>
 * 多个类拷贝融合而来，包括 {@link java.io.PrintWriter}、{@link javax.servlet.ServletOutputStream}、{@link java.io.OutputStreamWriter} 等等。
 * <p>
 * 可以的话还是应该继承自 {@link cn.hutool.core.io.FastByteArrayOutputStream} 或者 {@link org.springframework.util.FastByteArrayOutputStream} 等实现，此处为了不依赖其他 lib，所以选择继承了 {@link ByteArrayOutputStream}。
 *
 * @author zero
 * @since 2022/11/28
 */
public class ByteArrayOutputStreamWriter extends ByteArrayOutputStream implements Appendable {
	protected final Charset charset;
	protected final String nullDefault;

	public ByteArrayOutputStreamWriter() {
		this(StandardCharsets.UTF_8, "null");
	}

	public ByteArrayOutputStreamWriter(Charset charset) {
		this(charset, "null");
	}

	public ByteArrayOutputStreamWriter(String nullDefault) {
		this(StandardCharsets.UTF_8, nullDefault);
	}

	public ByteArrayOutputStreamWriter(Charset charset, String nullDefault) {
		this.charset = charset;
		this.nullDefault = nullDefault;
	}

	public void write(char[] chars) {
		this.write(chars, 0, chars.length);
	}

	@SneakyThrows
	public void write(char[] chars, int off, int len) {
		CharsetEncoder charsetEncoder = charset.newEncoder();
		CharBuffer charBuffer = CharBuffer.wrap(chars, off, len);
		ByteBuffer byteBuffer = charsetEncoder.encode(charBuffer);
		byte[] bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		this.write(bytes);
	}

	public void write(String str) {
		this.write(str, 0, str.length());
	}

	public void write(String str, int off, int len) {
		char[] chars;
		if (Objects.isNull(str)) {
			chars = nullDefault.toCharArray();
		} else {
			chars = new char[len];
			str.getChars(off, (off + len), chars, 0);
		}
		this.write(chars);
	}

	@Override
	public ByteArrayOutputStreamWriter append(CharSequence csq) {
		if (Objects.isNull(csq)) {
			this.write(nullDefault);
		} else {
			this.write(csq.toString());
		}
		return this;
	}

	@Override
	public ByteArrayOutputStreamWriter append(CharSequence csq, int start, int end) {
		CharSequence cs = Objects.isNull(csq) ? nullDefault : csq;
		this.write(cs.subSequence(start, end).toString());
		return this;
	}

	@Override
	public ByteArrayOutputStreamWriter append(char c) {
		this.write(c);
		return this;
	}

	public void print(boolean b) {
		this.print(b ? "true" : "false");
	}

	public void print(char c) {
		this.write(c);
	}

	public void print(int i) {
		this.write(String.valueOf(i));
	}

	public void print(long l) {
		this.write(String.valueOf(l));
	}

	public void print(float f) {
		this.write(String.valueOf(f));
	}

	public void print(double d) {
		this.write(String.valueOf(d));
	}

	public void print(char[] chars) {
		this.write(chars);
	}

	public void print(String s) {
		this.write(s);
	}

	public void print(Object obj) {
		this.write(Objects.toString(obj, nullDefault));
	}

	public void println() {
		this.print("\r\n");
	}

	public synchronized void println(boolean b) {
		this.print(b);
		this.println();
	}

	public synchronized void println(char c) {
		this.print(c);
		this.println();
	}

	public synchronized void println(int i) {
		this.print(i);
		this.println();
	}


	public synchronized void println(long l) {
		this.print(l);
		this.println();
	}

	public synchronized void println(float f) {
		this.print(f);
		this.println();
	}

	public synchronized void println(double d) {
		this.print(d);
		this.println();
	}

	public synchronized void println(char[] chars) {
		this.print(chars);
		this.println();
	}

	public synchronized void println(String s) {
		this.print(s);
		this.println();
	}

	public synchronized void println(Object obj) {
		this.print(obj);
		this.println();
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

	protected Formatter formatter;

	private synchronized void format(Locale locale, String format, Object... args) {
		if (Objects.isNull(formatter) || formatter.locale() != locale) {
			formatter = new Formatter(this, locale);
		}
		formatter.format(locale, format, args);
	}
}
