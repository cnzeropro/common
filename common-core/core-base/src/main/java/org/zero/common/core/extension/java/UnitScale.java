package org.zero.common.core.extension.java;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 国际单位制 (SI) 词头/中国法定计量单位词头
 * <p>
 * 参考国际计量大会（CGPM）规范，包含 2022 年新增的四个词头。
 * {@link #symbol} 表示国际符号，{@link #power} 表示以 10 为底的指数。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/12/26
 */
@Getter
@RequiredArgsConstructor
public enum UnitScale {
	/* ************************************* 极大数 (从大到小) ************************************* */

	/**
	 * 昆[它]（quetta），10^30。
	 */
	QUETTA("Q", 30),
	/**
	 * 容[那]（ronna），10^27。
	 */
	RONNA("R", 27),
	/**
	 * 尧[它]（yotta），10^24。
	 */
	YOTTA("Y", 24),
	/**
	 * 泽[它]（zetta），10^21。
	 */
	ZETTA("Z", 21),
	/**
	 * 艾[可萨]（exa），10^18。
	 */
	EXA("E", 18),
	/**
	 * 拍[它]（peta），10^15。
	 */
	PETA("P", 15),
	/**
	 * 太[拉]（tera），10^12。
	 */
	TERA("T", 12),
	/**
	 * 吉[咖]（giga），10^9。
	 */
	GIGA("G", 9),
	/**
	 * 兆（mega），10^6。
	 */
	MEGA("M", 6),
	/**
	 * 千（kilo），10^3。
	 */
	KILO("k", 3),
	/**
	 * 百（hecto），10^2。
	 */
	HECTO("h", 2),
	/**
	 * 十（deca），10^1。
	 */
	DECA("da", 1),

	/* ************************************* 极小数 (从大到小) ************************************* */

	/**
	 * 分（deci），10^-1。
	 */
	DECI("d", -1),
	/**
	 * 厘（centi），10^-2。
	 */
	CENTI("c", -2),
	/**
	 * 毫（milli），10^-3。
	 */
	MILLI("m", -3),
	/**
	 * 微（micro），10^-6。
	 */
	MICRO("μ", -6),
	/**
	 * 纳[诺]（nano），10^-9。
	 */
	NANO("n", -9),
	/**
	 * 皮[可]（pico），10^-12。
	 */
	PICO("p", -12),
	/**
	 * 飞[母托]（femto），10^-15。
	 */
	FEMTO("f", -15),
	/**
	 * 阿[托]（atto），10^-18。
	 */
	ATTO("a", -18),
	/**
	 * 仄[普托]（zepto），10^-21。
	 */
	ZEPTO("z", -21),
	/**
	 * 幺[科托]（yocto），10^-24。
	 */
	YOCTO("y", -24),
	/**
	 * 柔[托]（ronto），10^-27。
	 */
	RONTO("r", -27),
	/**
	 * 亏[科托]（quecto），10^-30。
	 */
	QUECTO("q", -30),
	;

	/**
	 * 国际符号，如 k、M、μ。
	 */
	private final String symbol;
	/**
	 * 以 10 为底的指数，如 kilo 为 3、milli 为 -3。
	 */
	private final int power;
}
