package org.zero.common.core.extension.java;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.lang.StringUtil;

import java.math.BigInteger;

import static org.zero.common.core.extension.java.DataSize.BINARY_RADIX;
import static org.zero.common.core.extension.java.DataSize.DECIMAL_RADIX;

/**
 * 数据单位
 * <p>
 * 支持 IEC 二进制单位（1024 为基数）和 SI 十进制单位（1000 为基数）。
 * 每个枚举值保存相对于 Byte 的换算倍率，供 {@link DataSize} 做解析、换算和可读化输出。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/12/24
 */
@Getter
@RequiredArgsConstructor
public enum DataUnit {
	/**
	 * 字节 - Byte (B)
	 * <p>
	 * 数据存储的基本单位，表示 8 位二进制数（1 Byte = 8 bits）
	 */
	BYTE(BigInteger.ONE, "B"),

	/* ****************************************** 二进制单位 (IEC Units) - 基于1024 ****************************************** */

	/**
	 * 千字节 - Kibibyte (KiB)
	 * <p>
	 * 2^10 = 1,024 bytes
	 */
	KIBIBYTE(BINARY_RADIX.pow(1), "KiB"),
	/**
	 * 兆字节 - Mebibyte (MiB)
	 * <p>
	 * 2^20 = 1,048,576 bytes
	 */
	MEBIBYTE(BINARY_RADIX.pow(2), "MiB"),
	/**
	 * 吉字节 - Gibibyte (GiB)
	 * <p>
	 * 2^30 = 1,073,741,824 bytes
	 */
	GIBIBYTE(BINARY_RADIX.pow(3), "GiB"),
	/**
	 * 太字节 - Tebibyte (TiB)
	 * <p>
	 * 2^40 = 1,099,511,627,776 bytes
	 */
	TEBIBYTE(BINARY_RADIX.pow(4), "TiB"),
	/**
	 * 拍字节 - Pebibyte (PiB)
	 * <p>
	 * 2^50 = 1,125,899,906,842,624 bytes
	 */
	PEBIBYTE(BINARY_RADIX.pow(5), "PiB"),
	/**
	 * 艾字节 - Exbibyte (EiB)
	 * <p>
	 * 2^60 = 1,152,921,504,606,846,976 bytes
	 */
	EXBIBYTE(BINARY_RADIX.pow(6), "EiB"),
	/**
	 * 泽字节 - Zebibyte (ZiB)
	 * <p>
	 * 2^70 = 1,180,591,620,717,411,303,424 bytes
	 */
	ZEBIBYTE(BINARY_RADIX.pow(7), "ZiB"),
	/**
	 * 尧字节 - Yobibyte (YiB)
	 * <p>
	 * 2^80 = 1,208,925,819,614,629,174,706,176 bytes
	 */
	YOBIBYTE(BINARY_RADIX.pow(8), "YiB"),
	/**
	 * 容字节 - Robibyte (RiB)
	 * <p>
	 * 2^90 = 1,237,940,039,285,380,274,899,124,224 bytes
	 */
	ROBIBYTE(BINARY_RADIX.pow(9), "RiB"),
	/**
	 * 昆字节 - Quebibyte (QiB)
	 * <p>
	 * 2^100 = 1,267,650,600,228,229,401,496,703,205,376 bytes
	 */
	QUEBIBYTE(BINARY_RADIX.pow(10), "QiB"),

	/* ****************************************** 十进制单位 (SI Units) - 基于1000 ****************************************** */

	/**
	 * 千字节 - Kilobyte (KB)
	 * <p>
	 * 10^3 = 1,000 bytes
	 */
	KILOBYTE(DECIMAL_RADIX.pow(1), "KB"),
	/**
	 * 兆字节 - Megabyte (MB)
	 * <p>
	 * 10^6 = 1,000,000 bytes
	 */
	MEGABYTE(DECIMAL_RADIX.pow(2), "MB"),
	/**
	 * 吉字节 - Gigabyte (GB)
	 * <p>
	 * 10^9 = 1,000,000,000 bytes
	 */
	GIGABYTE(DECIMAL_RADIX.pow(3), "GB"),
	/**
	 * 太字节 - Terabyte (TB)
	 * <p>
	 * 10^12 = 1,000,000,000,000 bytes
	 */
	TERABYTE(DECIMAL_RADIX.pow(4), "TB"),
	/**
	 * 拍字节 - Petabyte (PB)
	 * <p>
	 * 10^15 = 1,000,000,000,000,000 bytes
	 */
	PETABYTE(DECIMAL_RADIX.pow(5), "PB"),
	/**
	 * 艾字节 - Exabyte (EB)
	 * <p>
	 * 10^18 = 1,000,000,000,000,000,000 bytes
	 */
	EXABYTE(DECIMAL_RADIX.pow(6), "EB"),
	/**
	 * 泽字节 - Zettabyte (ZB)
	 * <p>
	 * 10^21 = 1,000,000,000,000,000,000,000 bytes
	 */
	ZETTABYTE(DECIMAL_RADIX.pow(7), "ZB"),
	/**
	 * 尧字节 - Yottabyte (YB)
	 * <p>
	 * 10^24 = 1,000,000,000,000,000,000,000,000 bytes
	 */
	YOTTABYTE(DECIMAL_RADIX.pow(8), "YB"),
	/**
	 * 容字节 - Ronnabyte (RB)
	 * <p>
	 * 10^27 = 1,000,000,000,000,000,000,000,000,000 bytes
	 */
	RONNABYTE(DECIMAL_RADIX.pow(9), "RB"),
	/**
	 * 昆字节 - Quettabyte (QB)
	 * <p>
	 * 10^30 = 1,000,000,000,000,000,000,000,000,000,000 bytes
	 */
	QUETTABYTE(DECIMAL_RADIX.pow(10), "QB"),
	;

	/**
	 * 当前单位对应的字节数倍率。
	 */
	private final BigInteger size;
	/**
	 * 单位后缀，用于文本解析和格式化输出。
	 */
	private final String suffix;

	/**
	 * 通过后缀获取对应的数据单位
	 * <p>
	 * 完整后缀优先精确匹配，如 MB 匹配十进制单位，MiB 匹配二进制单位。
	 * 未明确写成二进制或十进制的缩写默认使用二进制单位，如 M 默认匹配 MiB。
	 *
	 * @param suffix 单位后缀
	 * @return 匹配到的单位
	 * @throws IllegalArgumentException 后缀无法识别报错
	 */
	public static DataUnit fromSuffix(String suffix) {
		String suffixToUse = StringUtil.trim(suffix);
		if (StringUtil.isBlank(suffixToUse)) {
			throw new IllegalArgumentException("Data unit suffix must not be blank");
		}
		for (DataUnit unit : values()) {
			if (StringUtil.equals(unit.suffix, suffixToUse, true)) {
				return unit;
			}
		}
		for (DataUnit unit : values()) {
			// 支持类似于 3M、3m 等缩写写法，完整后缀已在上方优先处理
			if (StringUtil.startWith(unit.suffix, suffixToUse, true)) {
				return unit;
			}
		}
		throw new IllegalArgumentException("Unknown data unit suffix '" + suffix + "'");
	}

	/**
	 * 通过枚举名称获取对应的数据单位。
	 *
	 * @param name 枚举名称，不区分大小写
	 * @return 匹配到的单位
	 * @throws IllegalArgumentException 名称无法识别时报错
	 */
	public static DataUnit fromName(String name) {
		for (DataUnit unit : values()) {
			if (StringUtil.equals(unit.name(), name, true)) {
				return unit;
			}
		}
		throw new IllegalArgumentException("Unknown data unit name '" + name + "'");
	}
}
