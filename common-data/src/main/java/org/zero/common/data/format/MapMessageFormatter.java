package org.zero.common.data.format;

import java.util.Locale;
import java.util.Map;

/**
 * 基于命名参数（{@link Map}）的消息格式化器。
 *
 * <p>与 {@link MessageFormatter}（位置参数）相对应，本接口使用 {@link Map} 作为参数来源，
 * 占位符通常为键名形式（如 {@code {name}}、{@code {key}}），由实现类定义具体语法。</p>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/26
 * @see MessageFormatter
 */
@FunctionalInterface
public interface MapMessageFormatter {

	/**
	 * 格式化消息模板。
	 *
	 * @param pattern 消息模板，为 {@code null} 时由实现类决定返回值
	 * @param locale  格式化 Locale，为 {@code null} 时使用默认 Locale
	 * @param args    命名参数映射
	 * @return 格式化后的字符串，可能为 {@code null}
	 */
	String format(CharSequence pattern, Locale locale, Map<?, ?> args);

	/**
	 * 格式化消息模板（使用默认 Locale）。
	 *
	 * @param pattern 消息模板
	 * @param args    命名参数映射
	 * @return 格式化后的字符串，可能为 {@code null}
	 */
	default String format(CharSequence pattern, Map<?, ?> args) {
		return this.format(pattern, null, args);
	}
}
