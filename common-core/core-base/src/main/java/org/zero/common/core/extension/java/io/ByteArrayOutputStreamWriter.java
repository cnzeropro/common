package org.zero.common.core.extension.java.io;

import lombok.SneakyThrows;
import org.zero.common.data.constant.StringPool;

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
 * 同时支持字节写入与文本写入的内存输出流。
 * <p>
 * 该类继承自 {@link ByteArrayOutputStream}，保留原生的字节写入能力，并补充了针对 {@code char}、
 * {@link String}、{@link CharSequence} 以及类似 {@link java.io.PrintWriter} 的
 * {@code print}/{@code println}/{@code printf} 接口，便于在同一缓冲区内连续写入文本内容。
 * <p>
 * 注意事项：
 * <ul>
 *     <li>文本内容会按构造时指定的 {@link Charset} 编码，默认使用 {@link StandardCharsets#UTF_8}。</li>
 *     <li>{@link #toString()} 会按当前默认字符集解码整个缓冲区；如果缓冲区内混入了其他编码写入的原始字节，结果可能出现乱码。</li>
 *     <li>文本相关 API 收到 {@code null} 时，会写入 {@code nullDefault}；{@link #println()} 固定写入 {@code \r\n}。</li>
 * </ul>
 * <p>
 * 该实现参考了 {@link java.io.PrintWriter}、{@link javax.servlet.ServletOutputStream} 与
 * {@link java.io.OutputStreamWriter} 的部分接口风格。为了避免额外引入其他依赖，这里直接继承
 * {@link ByteArrayOutputStream}。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2022/11/28
 */
public class ByteArrayOutputStreamWriter extends ByteArrayOutputStream implements Appendable {
	protected final Charset charset;
	protected final String nullDefault;

	public ByteArrayOutputStreamWriter() {
		this(StandardCharsets.UTF_8, StringPool.NULL);
	}

	public ByteArrayOutputStreamWriter(Charset charset) {
		this(charset, StringPool.NULL);
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

	/**
	 * 按默认字符集将指定字符数组片段编码后写入当前缓冲区。
	 *
	 * @param chars 字符数组
	 * @param off   起始下标
	 * @param len   写入长度
	 */
	@SneakyThrows
	public void write(char[] chars, int off, int len) {
		CharsetEncoder charsetEncoder = charset.newEncoder();
		CharBuffer charBuffer = CharBuffer.wrap(chars, off, len);
		ByteBuffer byteBuffer = charsetEncoder.encode(charBuffer);
		byte[] bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		this.write(bytes);
	}

	/**
	 * 写入整个字符串；当参数为 {@code null} 时写入 {@code nullDefault}。
	 *
	 * @param str 字符串
	 */
	public void write(String str) {
		String value = Objects.isNull(str) ? nullDefault : str;
		this.write(value, 0, value.length());
	}

	/**
	 * 写入字符串片段；当参数为 {@code null} 时，会以 {@code nullDefault} 作为源字符串执行同样的切片逻辑。
	 *
	 * @param str 字符串
	 * @param off 起始下标
	 * @param len 写入长度
	 */
	public void write(String str, int off, int len) {
		String value = Objects.isNull(str) ? nullDefault : str;
		char[] chars = new char[len];
		value.getChars(off, off + len, chars, 0);
		this.write(chars);
	}

	@Override
	public ByteArrayOutputStreamWriter append(CharSequence csq) {
		String value = Objects.isNull(csq) ? nullDefault : csq.toString();
		this.write(value);
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
		this.write(String.valueOf(c));
		return this;
	}

	public void print(boolean b) {
		this.print(b ? "true" : "false");
	}

	public void print(char c) {
		this.write(String.valueOf(c));
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

	/**
	 * 固定写入 CRLF 换行符。
	 */
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

	/**
	 * 使用指定字符集解码当前缓冲区中的全部字节。
	 *
	 * @param charset 用于解码的字符集
	 * @return 解码后的字符串
	 */
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
