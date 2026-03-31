package org.zero.common.data.format;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.Objects;

/**
 * 基于 SLF4J {@code MessageFormatter} 的消息格式化器。
 *
 * <p>使用 SLF4J 内置的 {@link org.slf4j.helpers.MessageFormatter#basicArrayFormat(String, Object[])}
 * 进行格式化，占位符语法为 {@code {}}。该格式化器忽略 {@code locale} 参数，
 * 因 SLF4J 原生不支持本地化。</p>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/26
 * @see org.slf4j.helpers.MessageFormatter
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Slf4jMessageFormatter implements MessageFormatter {

	/**
	 * 全局单例。
	 */
	public static final Slf4jMessageFormatter INSTANCE = new Slf4jMessageFormatter();

	/**
	 * {@inheritDoc}
	 *
	 * <p><b>注意</b>：本实现忽略 {@code locale} 参数。</p>
	 */
	@Override
	public String format(CharSequence pattern, Locale locale, Object... args) {
		String actualPattern = Objects.isNull(pattern) ? null : pattern.toString();
		Object[] actualArgs = Objects.isNull(args) ? new Object[0] : args;
		return org.slf4j.helpers.MessageFormatter.basicArrayFormat(actualPattern, actualArgs);
	}
}
