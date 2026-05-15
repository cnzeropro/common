package org.zero.common.core.extension.java;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.lang.CharSequenceUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.zero.common.core.extension.java.DataUnit.BYTE;
import static org.zero.common.core.extension.java.DataUnit.EXABYTE;
import static org.zero.common.core.extension.java.DataUnit.EXBIBYTE;
import static org.zero.common.core.extension.java.DataUnit.GIBIBYTE;
import static org.zero.common.core.extension.java.DataUnit.GIGABYTE;
import static org.zero.common.core.extension.java.DataUnit.KIBIBYTE;
import static org.zero.common.core.extension.java.DataUnit.KILOBYTE;
import static org.zero.common.core.extension.java.DataUnit.MEBIBYTE;
import static org.zero.common.core.extension.java.DataUnit.MEGABYTE;
import static org.zero.common.core.extension.java.DataUnit.PEBIBYTE;
import static org.zero.common.core.extension.java.DataUnit.PETABYTE;
import static org.zero.common.core.extension.java.DataUnit.QUEBIBYTE;
import static org.zero.common.core.extension.java.DataUnit.QUETTABYTE;
import static org.zero.common.core.extension.java.DataUnit.ROBIBYTE;
import static org.zero.common.core.extension.java.DataUnit.RONNABYTE;
import static org.zero.common.core.extension.java.DataUnit.TEBIBYTE;
import static org.zero.common.core.extension.java.DataUnit.TERABYTE;
import static org.zero.common.core.extension.java.DataUnit.YOBIBYTE;
import static org.zero.common.core.extension.java.DataUnit.YOTTABYTE;
import static org.zero.common.core.extension.java.DataUnit.ZEBIBYTE;
import static org.zero.common.core.extension.java.DataUnit.ZETTABYTE;

/**
 * 数据大小
 * <p>
 * 以字节数作为内部存储单位，支持 IEC 二进制单位（1024 为基数）和 SI 十进制单位（1000 为基数）。
 * <p>
 * 文本解析允许小数单位，但换算结果必须是完整字节；负数会被保留，常用于表达差值或配额变化。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/12/24
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class DataSize implements Comparable<DataSize>, Serializable {
	/**
	 * 二进制单位基数。
	 */
	public static final long BINARY_BASE = 1024L;
	public static final BigInteger BINARY_RADIX = BigInteger.valueOf(BINARY_BASE);
	/**
	 * 十进制单位基数。
	 */
	public static final long DECIMAL_BASE = 1000L;
	public static final BigInteger DECIMAL_RADIX = BigInteger.valueOf(DECIMAL_BASE);

	/**
	 * 数据大小解析表达式：数字部分支持正负号和小数，单位后缀最多 3 位字母。
	 */
	public static final Pattern PATTERN = Pattern.compile("^([+-]?\\d+(\\.\\d+)?)\\s*([a-zA-Z]{0,3})$");

	/**
	 * 零字节常量。
	 */
	public static final DataSize ZERO = ofBytes(0L);

	/**
	 * 实际字节数。
	 */
	protected final BigInteger bytes;

	/**
	 * 解析数据大小文本，未提供单位时默认按 Byte 处理。
	 *
	 * @param text 数据大小文本，如 {@code 1.5MiB}、{@code 10MB}、{@code 512}
	 * @return 数据大小
	 * @throws IllegalArgumentException 文本为空、格式不匹配、单位未知或无法换算为完整字节时报错
	 */
	public static DataSize parse(CharSequence text) {
		return parse(text, null);
	}

	/**
	 * 解析数据大小文本，并在文本未带单位时使用指定默认单位。
	 *
	 * @param text        数据大小文本
	 * @param defaultUnit 默认单位，为 {@code null} 时使用 Byte
	 * @return 数据大小
	 * @throws IllegalArgumentException 文本为空、格式不匹配、单位未知或无法换算为完整字节时报错
	 */
	public static DataSize parse(CharSequence text, DataUnit defaultUnit) {
		if (CharSequenceUtil.isBlank(text)) {
			throw new IllegalArgumentException("Text must not be blank");
		}
		Matcher matcher = PATTERN.matcher(CharSequenceUtil.trim(text));
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Does not match data size pattern");
		}
		String suffix = matcher.group(3);
		DataUnit defaultUnitToUse = Objects.nonNull(defaultUnit) ? defaultUnit : BYTE;
		DataUnit unit = CharSequenceUtil.nonBlank(suffix) ? DataUnit.fromSuffix(suffix) : defaultUnitToUse;
		return of(new BigDecimal(matcher.group(1)), unit);
	}

	/**
	 * 创建指定字节数的数据大小。
	 *
	 * @param bytes 字节数
	 * @return 数据大小
	 */
	public static DataSize ofBytes(long bytes) {
		return ofBytes(BigInteger.valueOf(bytes));
	}

	/**
	 * 创建指定字节数的数据大小。
	 *
	 * @param bytes 字节数
	 * @return 数据大小
	 */
	public static DataSize ofBytes(BigInteger bytes) {
		return new DataSize(bytes);
	}

	public static DataSize of(long amount, String unit) {
		return of(amount, DataUnit.fromSuffix(unit));
	}

	public static DataSize of(BigInteger amount, String unit) {
		return of(amount, DataUnit.fromSuffix(unit));
	}

	public static DataSize of(double amount, String unit) {
		return of(amount, DataUnit.fromSuffix(unit));
	}

	public static DataSize of(BigDecimal amount, String unit) {
		return of(amount, DataUnit.fromSuffix(unit));
	}

	public static DataSize of(long amount, DataUnit unit) {
		return of(BigInteger.valueOf(amount), unit);
	}

	public static DataSize of(BigInteger amount, DataUnit unit) {
		return ofBytes(amount.multiply(unit.getSize()));
	}

	public static DataSize of(double amount, DataUnit unit) {
		return of(new BigDecimal(Double.toString(amount)), unit);
	}

	/**
	 * 按指定单位创建数据大小。
	 * <p>
	 * 换算结果必须是完整字节，例如 {@code 1.5KiB} 可以换算为 1536B，{@code 1.9B} 会报错。
	 *
	 * @param amount 数值
	 * @param unit   单位
	 * @return 数据大小
	 * @throws IllegalArgumentException 换算结果不是完整字节时报错
	 */
	public static DataSize of(BigDecimal amount, DataUnit unit) {
		try {
			// 换算结果必须落在完整字节上，避免 1.9B 这类输入被静默截断
			return ofBytes(amount.multiply(new BigDecimal(unit.getSize())).toBigIntegerExact());
		} catch (ArithmeticException exception) {
			throw new IllegalArgumentException("Data size must be an exact number of bytes", exception);
		}
	}

	/**
	 * 获取字节数。
	 *
	 * @return 字节数
	 */
	public BigInteger toBytes() {
		return this.bytes;
	}

	/**
	 * 转换为指定单位，结果只保留整数部分。
	 *
	 * @param unit 目标单位
	 * @return 目标单位下的整数值
	 */
	public BigInteger to(DataUnit unit) {
		return this.bytes.divide(unit.getSize());
	}

	/**
	 * 转换为指定单位。
	 *
	 * @param unit  目标单位
	 * @param scale 小数位数
	 * @return 目标单位下的十进制值
	 */
	public BigDecimal to(DataUnit unit, int scale) {
		return this.to(unit, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 转换为指定单位。
	 *
	 * @param unit         目标单位
	 * @param scale        小数位数
	 * @param roundingMode 舍入模式
	 * @return 目标单位下的十进制值
	 */
	public BigDecimal to(DataUnit unit, int scale, RoundingMode roundingMode) {
		return new BigDecimal(this.bytes).divide(new BigDecimal(unit.getSize()), scale, roundingMode);
	}

	@Override
	public int compareTo(DataSize other) {
		return this.bytes.compareTo(other.bytes);
	}

	@Override
	public String toString() {
		return String.format("%dB", this.bytes);
	}

	/**
	 * 输出默认可读字符串，默认使用二进制单位并保留 2 位小数。
	 *
	 * @return 可读字符串
	 */
	public String toReadableString() {
		return this.toReadableString(true);
	}

	/**
	 * 输出可读字符串。
	 *
	 * @param useBinaryUnits 是否使用二进制单位；{@code false} 时使用十进制单位
	 * @return 可读字符串
	 */
	public String toReadableString(boolean useBinaryUnits) {
		return this.toReadableString(useBinaryUnits, 2);
	}

	/**
	 * 输出可读字符串。
	 *
	 * @param useBinaryUnits 是否使用二进制单位；{@code false} 时使用十进制单位
	 * @param scale          小数位数
	 * @return 可读字符串
	 */
	public String toReadableString(boolean useBinaryUnits, int scale) {
		return this.toReadableString(useBinaryUnits, scale, RoundingMode.HALF_UP);
	}

	/**
	 * 输出可读字符串。
	 *
	 * @param useBinaryUnits 是否使用二进制单位；{@code false} 时使用十进制单位
	 * @param scale          小数位数
	 * @param roundingMode   舍入模式
	 * @return 可读字符串
	 */
	public String toReadableString(boolean useBinaryUnits, int scale, RoundingMode roundingMode) {
		if (useBinaryUnits) {
			return this.toBinaryReadableString(scale, roundingMode);
		}
		return this.toDecimalReadableString(scale, roundingMode);
	}

	/**
	 * 使用 IEC 二进制单位输出可读字符串。
	 * <p>
	 * 单位选择按字节数绝对值判断，最终数值仍保留原始正负号。
	 *
	 * @param scale        小数位数
	 * @param roundingMode 舍入模式
	 * @return 可读字符串
	 */
	public String toBinaryReadableString(int scale, RoundingMode roundingMode) {
		BigInteger absoluteBytes = this.bytes.abs();
		if (absoluteBytes.compareTo(KIBIBYTE.getSize()) < 0) {
			return String.format("%dB", this.bytes);
		}
		if (absoluteBytes.compareTo(MEBIBYTE.getSize()) < 0) {
			return this.to(KIBIBYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + KIBIBYTE.getSuffix();
		}
		if (absoluteBytes.compareTo(GIBIBYTE.getSize()) < 0) {
			return this.to(MEBIBYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + MEBIBYTE.getSuffix();
		}
		if (absoluteBytes.compareTo(TEBIBYTE.getSize()) < 0) {
			return this.to(GIBIBYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + GIBIBYTE.getSuffix();
		}
		if (absoluteBytes.compareTo(PEBIBYTE.getSize()) < 0) {
			return this.to(TEBIBYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + TEBIBYTE.getSuffix();
		}
		if (absoluteBytes.compareTo(EXBIBYTE.getSize()) < 0) {
			return this.to(PEBIBYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + PEBIBYTE.getSuffix();
		}
		if (absoluteBytes.compareTo(ZEBIBYTE.getSize()) < 0) {
			return this.to(EXBIBYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + EXBIBYTE.getSuffix();
		}
		if (absoluteBytes.compareTo(YOBIBYTE.getSize()) < 0) {
			return this.to(ZEBIBYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + ZEBIBYTE.getSuffix();
		}
		if (absoluteBytes.compareTo(ROBIBYTE.getSize()) < 0) {
			return this.to(YOBIBYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + YOBIBYTE.getSuffix();
		}
		if (absoluteBytes.compareTo(QUEBIBYTE.getSize()) < 0) {
			return this.to(ROBIBYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + ROBIBYTE.getSuffix();
		}
		return this.to(QUEBIBYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + QUEBIBYTE.getSuffix();
	}

	/**
	 * 使用 SI 十进制单位输出可读字符串。
	 * <p>
	 * 单位选择按字节数绝对值判断，最终数值仍保留原始正负号。
	 *
	 * @param scale        小数位数
	 * @param roundingMode 舍入模式
	 * @return 可读字符串
	 */
	public String toDecimalReadableString(int scale, RoundingMode roundingMode) {
		BigInteger absoluteBytes = this.bytes.abs();
		if (absoluteBytes.compareTo(KILOBYTE.getSize()) < 0) {
			return String.format("%dB", this.bytes);
		}
		if (absoluteBytes.compareTo(MEGABYTE.getSize()) < 0) {
			return this.to(KILOBYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + KILOBYTE.getSuffix();
		}
		if (absoluteBytes.compareTo(GIGABYTE.getSize()) < 0) {
			return this.to(MEGABYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + MEGABYTE.getSuffix();
		}
		if (absoluteBytes.compareTo(TERABYTE.getSize()) < 0) {
			return this.to(GIGABYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + GIGABYTE.getSuffix();
		}
		if (absoluteBytes.compareTo(PETABYTE.getSize()) < 0) {
			return this.to(TERABYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + TERABYTE.getSuffix();
		}
		if (absoluteBytes.compareTo(EXABYTE.getSize()) < 0) {
			return this.to(PETABYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + PETABYTE.getSuffix();
		}
		if (absoluteBytes.compareTo(ZETTABYTE.getSize()) < 0) {
			return this.to(EXABYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + EXABYTE.getSuffix();
		}
		if (absoluteBytes.compareTo(YOTTABYTE.getSize()) < 0) {
			return this.to(ZETTABYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + ZETTABYTE.getSuffix();
		}
		if (absoluteBytes.compareTo(RONNABYTE.getSize()) < 0) {
			return this.to(YOTTABYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + YOTTABYTE.getSuffix();
		}
		if (absoluteBytes.compareTo(QUETTABYTE.getSize()) < 0) {
			return this.to(RONNABYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + RONNABYTE.getSuffix();
		}
		return this.to(QUETTABYTE, scale, roundingMode).stripTrailingZeros().toPlainString() + QUETTABYTE.getSuffix();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DataSize)) {
			return false;
		}
		DataSize otherSize = (DataSize) other;
		return this.bytes.equals(otherSize.bytes);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.bytes);
	}

	/* ****************************************** 二进制单位 (IEC Units) - 基于1024 ****************************************** */

	public static DataSize ofKibibytes(long kibibytes) {
		return of(kibibytes, KIBIBYTE);
	}

	public static DataSize ofKibibytes(BigInteger kibibytes) {
		return of(kibibytes, KIBIBYTE);
	}

	public static DataSize ofKibibytes(double kibibytes) {
		return of(kibibytes, KIBIBYTE);
	}

	public static DataSize ofKibibytes(BigDecimal kibibytes) {
		return of(kibibytes, KIBIBYTE);
	}

	public static DataSize ofMebibytes(long mebibytes) {
		return of(mebibytes, MEBIBYTE);
	}

	public static DataSize ofMebibytes(BigInteger mebibytes) {
		return of(mebibytes, MEBIBYTE);
	}

	public static DataSize ofMebibytes(double mebibytes) {
		return of(mebibytes, MEBIBYTE);
	}

	public static DataSize ofMebibytes(BigDecimal mebibytes) {
		return of(mebibytes, MEBIBYTE);
	}

	public static DataSize ofGibibytes(long gibibytes) {
		return of(gibibytes, GIBIBYTE);
	}

	public static DataSize ofGibibytes(BigInteger gibibytes) {
		return of(gibibytes, GIBIBYTE);
	}

	public static DataSize ofGibibytes(double gibibytes) {
		return of(gibibytes, GIBIBYTE);
	}

	public static DataSize ofGibibytes(BigDecimal gibibytes) {
		return of(gibibytes, GIBIBYTE);
	}

	public static DataSize ofTebibytes(long tebibytes) {
		return of(tebibytes, TEBIBYTE);
	}

	public static DataSize ofTebibytes(BigInteger tebibytes) {
		return of(tebibytes, TEBIBYTE);
	}

	public static DataSize ofTebibytes(double tebibytes) {
		return of(tebibytes, TEBIBYTE);
	}

	public static DataSize ofTebibytes(BigDecimal tebibytes) {
		return of(tebibytes, TEBIBYTE);
	}

	public static DataSize ofPebibytes(long pebibytes) {
		return of(pebibytes, PEBIBYTE);
	}

	public static DataSize ofPebibytes(BigInteger pebibytes) {
		return of(pebibytes, PEBIBYTE);
	}

	public static DataSize ofPebibytes(double pebibytes) {
		return of(pebibytes, PEBIBYTE);
	}

	public static DataSize ofPebibytes(BigDecimal pebibytes) {
		return of(pebibytes, PEBIBYTE);
	}

	public static DataSize ofExbibytes(long exbibytes) {
		return of(exbibytes, EXBIBYTE);
	}

	public static DataSize ofExbibytes(BigInteger exbibytes) {
		return of(exbibytes, EXBIBYTE);
	}

	public static DataSize ofExbibytes(double exbibytes) {
		return of(exbibytes, EXBIBYTE);
	}

	public static DataSize ofExbibytes(BigDecimal exbibytes) {
		return of(exbibytes, EXBIBYTE);
	}

	public static DataSize ofZebibytes(long zebibytes) {
		return of(zebibytes, ZEBIBYTE);
	}

	public static DataSize ofZebibytes(BigInteger zebibytes) {
		return of(zebibytes, ZEBIBYTE);
	}

	public static DataSize ofZebibytes(double zebibytes) {
		return of(zebibytes, ZEBIBYTE);
	}

	public static DataSize ofZebibytes(BigDecimal zebibytes) {
		return of(zebibytes, ZEBIBYTE);
	}

	public static DataSize ofYobibytes(long yobibytes) {
		return of(yobibytes, YOBIBYTE);
	}

	public static DataSize ofYobibytes(BigInteger yobibytes) {
		return of(yobibytes, YOBIBYTE);
	}

	public static DataSize ofYobibytes(double yobibytes) {
		return of(yobibytes, YOBIBYTE);
	}

	public static DataSize ofYobibytes(BigDecimal yobibytes) {
		return of(yobibytes, YOBIBYTE);
	}

	public static DataSize ofRobibytes(long robibytes) {
		return of(robibytes, ROBIBYTE);
	}

	public static DataSize ofRobibytes(BigInteger robibytes) {
		return of(robibytes, ROBIBYTE);
	}

	public static DataSize ofRobibytes(double robibytes) {
		return of(robibytes, ROBIBYTE);
	}

	public static DataSize ofRobibytes(BigDecimal robibytes) {
		return of(robibytes, ROBIBYTE);
	}

	public static DataSize ofQuebibytes(long quebibytes) {
		return of(quebibytes, QUEBIBYTE);
	}

	public static DataSize ofQuebibytes(BigInteger quebibytes) {
		return of(quebibytes, QUEBIBYTE);
	}

	public static DataSize ofQuebibytes(double quebibytes) {
		return of(quebibytes, QUEBIBYTE);
	}

	public static DataSize ofQuebibytes(BigDecimal quebibytes) {
		return of(quebibytes, QUEBIBYTE);
	}

	public BigInteger toKibibytes() {
		return this.to(KIBIBYTE);
	}

	public BigDecimal toKibibytes(int scale) {
		return this.to(KIBIBYTE, scale, RoundingMode.HALF_UP);
	}

	public BigDecimal toKibibytes(int scale, RoundingMode roundingMode) {
		return this.to(KIBIBYTE, scale, roundingMode);
	}

	public BigInteger toMebibytes() {
		return this.to(MEBIBYTE);
	}

	public BigDecimal toMebibytes(int scale) {
		return this.to(MEBIBYTE, scale, RoundingMode.HALF_UP);
	}

	public BigDecimal toMebibytes(int scale, RoundingMode roundingMode) {
		return this.to(MEBIBYTE, scale, roundingMode);
	}

	public BigInteger toGibibytes() {
		return this.to(GIBIBYTE);
	}

	public BigDecimal toGibibytes(int scale) {
		return this.to(GIBIBYTE, scale, RoundingMode.HALF_UP);
	}

	public BigDecimal toGibibytes(int scale, RoundingMode roundingMode) {
		return this.to(GIBIBYTE, scale, roundingMode);
	}

	public BigInteger toTebibytes() {
		return this.to(TEBIBYTE);
	}

	public BigDecimal toTebibytes(int scale) {
		return this.to(TEBIBYTE, scale, RoundingMode.HALF_UP);
	}

	public BigDecimal toTebibytes(int scale, RoundingMode roundingMode) {
		return this.to(TEBIBYTE, scale, roundingMode);
	}

	public BigInteger toPebibytes() {
		return this.to(PEBIBYTE);
	}

	public BigDecimal toPebibytes(int scale) {
		return this.to(PEBIBYTE, scale, RoundingMode.HALF_UP);
	}

	public BigDecimal toPebibytes(int scale, RoundingMode roundingMode) {
		return this.to(PEBIBYTE, scale, roundingMode);
	}

	public BigInteger toExbibytes() {
		return this.to(EXBIBYTE);
	}

	public BigDecimal toExbibytes(int scale) {
		return this.to(EXBIBYTE, scale, RoundingMode.HALF_UP);
	}

	public BigDecimal toExbibytes(int scale, RoundingMode roundingMode) {
		return this.to(EXBIBYTE, scale, roundingMode);
	}

	public BigInteger toZebibytes() {
		return this.to(ZEBIBYTE);
	}

	public BigDecimal toZebibytes(int scale) {
		return this.to(ZEBIBYTE, scale, RoundingMode.HALF_UP);
	}

	public BigDecimal toZebibytes(int scale, RoundingMode roundingMode) {
		return this.to(ZEBIBYTE, scale, roundingMode);
	}

	public BigInteger toYobibytes() {
		return this.to(YOBIBYTE);
	}

	public BigDecimal toYobibytes(int scale) {
		return this.to(YOBIBYTE, scale, RoundingMode.HALF_UP);
	}

	public BigDecimal toYobibytes(int scale, RoundingMode roundingMode) {
		return this.to(YOBIBYTE, scale, roundingMode);
	}

	/* ****************************************** 十进制单位 (SI Units) - 基于1000 ****************************************** */
	public static DataSize ofKilobytes(long kilobytes) {
		return of(kilobytes, KILOBYTE);
	}

	public static DataSize ofKilobytes(BigInteger kilobytes) {
		return of(kilobytes, KILOBYTE);
	}

	public static DataSize ofKilobytes(double kilobytes) {
		return of(kilobytes, KILOBYTE);
	}

	public static DataSize ofKilobytes(BigDecimal kilobytes) {
		return of(kilobytes, KILOBYTE);
	}

	public static DataSize ofMegabytes(long megabytes) {
		return of(megabytes, MEGABYTE);
	}

	public static DataSize ofMegabytes(BigInteger megabytes) {
		return of(megabytes, MEGABYTE);
	}

	public static DataSize ofMegabytes(double megabytes) {
		return of(megabytes, MEGABYTE);
	}

	public static DataSize ofMegabytes(BigDecimal megabytes) {
		return of(megabytes, MEGABYTE);
	}

	public static DataSize ofGigabytes(long gigabytes) {
		return of(gigabytes, GIGABYTE);
	}

	public static DataSize ofGigabytes(BigInteger gigabytes) {
		return of(gigabytes, GIGABYTE);
	}

	public static DataSize ofGigabytes(double gigabytes) {
		return of(gigabytes, GIGABYTE);
	}

	public static DataSize ofGigabytes(BigDecimal gigabytes) {
		return of(gigabytes, GIGABYTE);
	}

	public static DataSize ofTerabytes(long terabytes) {
		return of(terabytes, TERABYTE);
	}

	public static DataSize ofTerabytes(BigInteger terabytes) {
		return of(terabytes, TERABYTE);
	}

	public static DataSize ofTerabytes(double terabytes) {
		return of(terabytes, TERABYTE);
	}

	public static DataSize ofTerabytes(BigDecimal terabytes) {
		return of(terabytes, TERABYTE);
	}

	public static DataSize ofPetabytes(long petabytes) {
		return of(petabytes, PETABYTE);
	}

	public static DataSize ofPetabytes(BigInteger petabytes) {
		return of(petabytes, PETABYTE);
	}

	public static DataSize ofPetabytes(double petabytes) {
		return of(petabytes, PETABYTE);
	}

	public static DataSize ofPetabytes(BigDecimal petabytes) {
		return of(petabytes, PETABYTE);
	}

	public static DataSize ofExabytes(long exabytes) {
		return of(exabytes, EXABYTE);
	}

	public static DataSize ofExabytes(BigInteger exabytes) {
		return of(exabytes, EXABYTE);
	}

	public static DataSize ofExabytes(double exabytes) {
		return of(exabytes, EXABYTE);
	}

	public static DataSize ofExabytes(BigDecimal exabytes) {
		return of(exabytes, EXABYTE);
	}

	public static DataSize ofZettabytes(long zettabytes) {
		return of(zettabytes, ZETTABYTE);
	}

	public static DataSize ofZettabytes(BigInteger zettabytes) {
		return of(zettabytes, ZETTABYTE);
	}

	public static DataSize ofZettabytes(double zettabytes) {
		return of(zettabytes, ZETTABYTE);
	}

	public static DataSize ofZettabytes(BigDecimal zettabytes) {
		return of(zettabytes, ZETTABYTE);
	}

	public static DataSize ofYottabytes(long yottabytes) {
		return of(yottabytes, YOTTABYTE);
	}

	public static DataSize ofYottabytes(BigInteger yottabytes) {
		return of(yottabytes, YOTTABYTE);
	}

	public static DataSize ofYottabytes(double yottabytes) {
		return of(yottabytes, YOTTABYTE);
	}

	public static DataSize ofYottabytes(BigDecimal yottabytes) {
		return of(yottabytes, YOTTABYTE);
	}

	public static DataSize ofRonnabytes(long ronnabytes) {
		return of(ronnabytes, RONNABYTE);
	}

	public static DataSize ofRonnabytes(BigInteger ronnabytes) {
		return of(ronnabytes, RONNABYTE);
	}

	public static DataSize ofRonnabytes(double ronnabytes) {
		return of(ronnabytes, RONNABYTE);
	}

	public static DataSize ofRonnabytes(BigDecimal ronnabytes) {
		return of(ronnabytes, RONNABYTE);
	}

	public static DataSize ofQuettabytes(long quettabytes) {
		return of(quettabytes, QUETTABYTE);
	}

	public static DataSize ofQuettabytes(BigInteger quettabytes) {
		return of(quettabytes, QUETTABYTE);
	}

	public static DataSize ofQuettabytes(double quettabytes) {
		return of(quettabytes, QUETTABYTE);
	}

	public static DataSize ofQuettabytes(BigDecimal quettabytes) {
		return of(quettabytes, QUETTABYTE);
	}

	public BigInteger toKilobytes() {
		return this.to(KILOBYTE);
	}

	public BigDecimal toKilobytes(int scale) {
		return this.to(KILOBYTE, scale, RoundingMode.HALF_UP);
	}

	public BigDecimal toKilobytes(int scale, RoundingMode roundingMode) {
		return this.to(KILOBYTE, scale, roundingMode);
	}

	public BigInteger toMegabytes() {
		return this.to(MEGABYTE);
	}

	public BigDecimal toMegabytes(int scale) {
		return this.to(MEGABYTE, scale, RoundingMode.HALF_UP);
	}

	public BigDecimal toMegabytes(int scale, RoundingMode roundingMode) {
		return this.to(MEGABYTE, scale, roundingMode);
	}

	public BigInteger toGigabytes() {
		return this.to(GIGABYTE);
	}

	public BigDecimal toGigabytes(int scale) {
		return this.to(GIGABYTE, scale, RoundingMode.HALF_UP);
	}

	public BigDecimal toGigabytes(int scale, RoundingMode roundingMode) {
		return this.to(GIGABYTE, scale, roundingMode);
	}

	public BigInteger toTerabytes() {
		return this.to(TERABYTE);
	}

	public BigDecimal toTerabytes(int scale) {
		return this.to(TERABYTE, scale, RoundingMode.HALF_UP);
	}

	public BigDecimal toTerabytes(int scale, RoundingMode roundingMode) {
		return this.to(TERABYTE, scale, roundingMode);
	}

	public BigInteger toPetabytes() {
		return this.to(PETABYTE);
	}

	public BigDecimal toPetabytes(int scale) {
		return this.to(PETABYTE, scale, RoundingMode.HALF_UP);
	}

	public BigDecimal toPetabytes(int scale, RoundingMode roundingMode) {
		return this.to(PETABYTE, scale, roundingMode);
	}

	public BigInteger toExabytes() {
		return this.to(EXABYTE);
	}

	public BigDecimal toExabytes(int scale) {
		return this.to(EXABYTE, scale, RoundingMode.HALF_UP);
	}

	public BigDecimal toExabytes(int scale, RoundingMode roundingMode) {
		return this.to(EXABYTE, scale, roundingMode);
	}

	public BigInteger toZettabytes() {
		return this.to(ZETTABYTE);
	}

	public BigDecimal toZettabytes(int scale) {
		return this.to(ZETTABYTE, scale, RoundingMode.HALF_UP);
	}

	public BigDecimal toZettabytes(int scale, RoundingMode roundingMode) {
		return this.to(ZETTABYTE, scale, roundingMode);
	}

	public BigInteger toYottabytes() {
		return this.to(YOTTABYTE);
	}

	public BigDecimal toYottabytes(int scale) {
		return this.to(YOTTABYTE, scale, RoundingMode.HALF_UP);
	}

	public BigDecimal toYottabytes(int scale, RoundingMode roundingMode) {
		return this.to(YOTTABYTE, scale, roundingMode);
	}
}
