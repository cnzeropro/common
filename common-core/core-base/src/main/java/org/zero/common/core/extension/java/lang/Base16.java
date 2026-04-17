package org.zero.common.core.extension.java.lang;

import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.data.exception.UtilException;

/**
 * 十六进制编解码工具。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/4
 */
public class Base16 {
	public static final Base16 LOWER = new Base16(true);
	public static final Base16 UPPER = new Base16(false);

	private final char[] alphabets;

	/**
	 * 构造。
	 *
	 * @param lowerCase 是否小写
	 */
	public Base16(boolean lowerCase) {
		this.alphabets = (lowerCase ? "0123456789abcdef" : "0123456789ABCDEF").toCharArray();
	}

	public char[] encode(byte[] data) {
		final int len = data.length;
		final char[] out = new char[len << 1];// len*2
		// two characters from the hex value.
		for (int i = 0, j = 0; i < len; i++) {
			out[j++] = alphabets[(0xF0 & data[i]) >>> 4];// 高位
			out[j++] = alphabets[0x0F & data[i]];// 低位
		}
		return out;
	}

	/**
	 * 将十六进制字符转换成一个整数。
	 *
	 * @param ch    十六进制 char
	 * @param index 十六进制字符在字符数组中的位置
	 * @return 一个整数
	 * @throws UtilException 当 ch 不是一个合法的十六进制字符时，抛出运行时异常
	 */
	protected static int toDigit(char ch, int index) {
		int digit = Character.digit(ch, 16);
		if (digit < 0) {
			throw new UtilException(String.format("Illegal hexadecimal character %s at index %s", ch, index));
		}
		return digit;
	}

	/**
	 * 将十六进制字符串解码为字节数组，默认在奇数长度时自动前补 {@code 0}。
	 *
	 * @param encoded 十六进制字符串
	 * @return 解码后的字节数组
	 * @throws UtilException 当包含非法十六进制字符时抛出
	 */
	public byte[] decode(CharSequence encoded) {
		return decode(encoded, true);
	}

	/**
	 * 将十六进制字符串解码为字节数组。
	 *
	 * @param encoded            十六进制字符串
	 * @param autoPadLeadingZero 奇数长度时是否自动前补 {@code 0}
	 * @return 解码后的字节数组
	 * @throws UtilException 当长度为奇数且不允许自动补位，或包含非法十六进制字符时抛出
	 */
	public byte[] decode(CharSequence encoded, boolean autoPadLeadingZero) {
		if (CharSequenceUtil.isEmpty(encoded)) {
			return new byte[0];
		}
		int len = encoded.length();
		if ((len & 0x01) != 0) {
			if (!autoPadLeadingZero) {
				throw new UtilException("Odd-length hexadecimal input is not allowed");
			}
			// 奇数长度时前补 0，保持兼容行为。
			encoded = "0" + encoded;
			len = encoded.length();
		}

		final byte[] out = new byte[len >> 1];

		// two characters form the hex value.
		for (int i = 0, j = 0; j < len; i++) {
			int f = toDigit(encoded.charAt(j), j) << 4;
			j++;
			f = f | toDigit(encoded.charAt(j), j);
			j++;
			out[i] = (byte) (f & 0xFF);
		}

		return out;
	}

	/**
	 * 将指定 char 值转换为 Unicode 字符串形式，常用于特殊字符（例如汉字）转 Unicode 形式。<br>
	 * 转换的字符串如果 u 后不足 4 位，则前面用 0 填充，例如：
	 * <pre>
	 * '你' = "\u4f60"
	 * </pre>
	 *
	 * @param ch char 值
	 * @return Unicode 表现形式
	 */
	public String toUnicodeHex(char ch) {
		return "\\u" +
			alphabets[(ch >> 12) & 15] +
			alphabets[(ch >> 8) & 15] +
				alphabets[(ch >> 4) & 15] +
				alphabets[ch & 15];
	}

	/**
	 * 将 byte 值转为 16 进制并追加到 {@link StringBuilder} 中。
	 *
	 * @param builder {@link StringBuilder}
	 * @param b       byte
	 */
	public void appendHex(StringBuilder builder, byte b) {
		int high = (b & 0xf0) >>> 4; // 高位
		int low = b & 0x0f; // 低位
		builder.append(alphabets[high]);
		builder.append(alphabets[low]);
	}
}
