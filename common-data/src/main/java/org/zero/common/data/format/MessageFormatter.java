package org.zero.common.data.format;

import java.util.Locale;

/**
 * 基于位置参数的消息格式化器。
 *
 * <p>将 {@code pattern} 中的占位符替换为 {@code args} 中的参数值，
 * 生成最终的格式化字符串。具体的占位符语法由实现类决定。</p>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/26
 * @see MapMessageFormatter
 */
@FunctionalInterface
public interface MessageFormatter {

	/**
	 * 格式化消息模板。
	 *
	 * @param pattern 消息模板，为 {@code null} 时由实现类决定返回值
	 * @param locale  格式化 Locale，为 {@code null} 时使用默认 Locale
	 * @param args    格式化参数
	 * @return 格式化后的字符串，可能为 {@code null}
	 */
	String format(CharSequence pattern, Locale locale, Object... args);

	/**
	 * 格式化消息模板（使用默认 Locale）。
	 *
	 * @param pattern 消息模板
	 * @param args    格式化参数
	 * @return 格式化后的字符串，可能为 {@code null}
	 */
	default String format(CharSequence pattern, Object... args) {
		return this.format(pattern, null, args);
	}
}
