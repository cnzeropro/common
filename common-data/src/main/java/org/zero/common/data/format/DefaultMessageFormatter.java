package org.zero.common.data.format;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;

/**
 * 基于 {@link MessageFormat} 的默认消息格式化器。
 *
 * <p>使用 Java 标准库 {@link java.text.MessageFormat} 进行格式化，
 * 占位符语法为 {@code {0}}、{@code {1}}… 等位置索引形式。
 * 支持通过 {@link Locale} 控制本地化格式（如数字、日期）。</p>
 *
 * <h3>行为说明</h3>
 * <ul>
 *     <li>{@code pattern} 为 {@code null} 时返回 {@code null}</li>
 *     <li>{@code args} 为 {@code null} 或空时，原样返回 pattern</li>
 *     <li>格式化异常时，以 {@link Level#CONFIG} 级别记录日志并返回异常消息</li>
 * </ul>
 *
 * @author Zero (cnzeropro@163.com)
 * @see MessageFormat
 * @since 2026/03/26
 */
@Log
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DefaultMessageFormatter implements MessageFormatter {

	/**
	 * 全局单例。
	 */
	public static final DefaultMessageFormatter INSTANCE = new DefaultMessageFormatter();

	/**
	 * {@inheritDoc}
	 *
	 * <p>{@code locale} 为 {@code null} 时使用默认 Locale。</p>
	 */
	@Override
	public String format(CharSequence pattern, Locale locale, Object... args) {
		if (Objects.isNull(pattern)) {
			return null;
		}
		String actualPattern = pattern.toString();
		if (Objects.isNull(args) || args.length <= 0) {
			return actualPattern;
		}
		try {
			MessageFormat messageFormat;
			if (Objects.isNull(locale)) {
				messageFormat = new MessageFormat(actualPattern);
			} else {
				messageFormat = new MessageFormat(actualPattern, locale);
			}
			return messageFormat.format(args);
		} catch (Exception e) {
			log.log(Level.CONFIG, String.format("Message formatting failed, pattern: %s, args: %s", actualPattern, Arrays.toString(args)), e);
			return actualPattern;
		}
	}
}
