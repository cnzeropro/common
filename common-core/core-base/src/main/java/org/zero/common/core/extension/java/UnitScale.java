package org.zero.common.core.extension.java;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 国际单位制 (SI) 词头 / 中国法定计量单位词头
 * <p>
 * 参考国际计量大会（CGPM）规范，包含 2022 年新增的四个词头
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/12/26
 */
@Getter
@RequiredArgsConstructor
public enum UnitScale {
	/* ************************************* 极大数 (从大到小) ************************************* */

	/**
	 * 昆[它]
	 */
	QUETTA("Q", 30),
	/**
	 * 容[那]
	 */
	RONNA("R", 27),
	/**
	 * 尧[它]
	 */
	YOTTA("Y", 24),
	/**
	 * 泽[它]
	 */
	ZETTA("Z", 21),
	/**
	 * 艾[可萨]
	 */
	EXA("E", 18),
	/**
	 * 拍[它]
	 */
	PETA("P", 15),
	/**
	 * 太[拉]
	 */
	TERA("T", 12),
	/**
	 * 吉[咖]
	 */
	GIGA("G", 9),
	/**
	 * 兆
	 */
	MEGA("M", 6),
	/**
	 * 千
	 */
	KILO("k", 3),
	/**
	 * 百
	 */
	HECTO("h", 2),
	/**
	 * 十
	 */
	DECA("da", 1),

	/* ************************************* 极小数 (从大到小) ************************************* */

	/**
	 * 分
	 */
	DECI("d", -1),
	/**
	 * 厘
	 */
	CENTI("c", -2),
	/**
	 * 毫
	 */
	MILLI("m", -3),
	/**
	 * 微
	 */
	MICRO("μ", -6),
	/**
	 * 纳[诺]
	 */
	NANO("n", -9),
	/**
	 * 皮[可]
	 */
	PICO("p", -12),
	/**
	 * 飞[母托]
	 */
	FEMTO("f", -15),
	/**
	 * 阿[托]
	 */
	ATTO("a", -18),
	/**
	 * 仄[普托]
	 */
	ZEPTO("z", -21),
	/**
	 * 幺[科托]
	 */
	YOCTO("y", -24),
	/**
	 * 柔[托]
	 */
	RONTO("r", -27),
	/**
	 * 亏[科托]
	 */
	QUECTO("q", -30),
	;

	/**
	 * 国际符号
	 */
	private final String symbol;
	/**
	 * 幂次
	 */
	private final int power;
}
