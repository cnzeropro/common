package org.zero.common.data.format;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.text.StrPool;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.Objects;

/**
 * 基于 Hutool {@link StrFormatter} 的消息格式化器。
 *
 * <p>使用 Hutool 的 {@link StrFormatter#format(String, String, Object...)} 进行格式化，
 * 占位符通过构造参数 {@link #placeHolder} 自定义，默认为 {@link StrPool#EMPTY_JSON}（即 {@code {}}）。
 * 本实现忽略 {@code locale} 参数。</p>
 *
 * @author Zero (cnzeropro@163.com)
 * @see StrFormatter
 * @since 2026/03/27
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class HutoolMessageFormatter implements MessageFormatter {

	/**
	 * 默认单例，使用 {@link StrPool#EMPTY_JSON}（{@code {}}）作为占位符。
	 */
	public static final HutoolMessageFormatter INSTANCE = new HutoolMessageFormatter(StrPool.EMPTY_JSON);

	/**
	 * 占位符字符串，由 {@link StrFormatter} 用于匹配并替换参数。
	 */
	protected final String placeHolder;

	/**
	 * {@inheritDoc}
	 *
	 * <p><b>注意</b>：本实现忽略 {@code locale} 参数。</p>
	 */
	@Override
	public String format(CharSequence pattern, Locale locale, Object... args) {
		String actualPattern = Objects.isNull(pattern) ? null : pattern.toString();
		Object[] actualArgs = Objects.isNull(args) ? new Object[0] : args;
		return StrFormatter.format(actualPattern, placeHolder, actualArgs);
	}
}
