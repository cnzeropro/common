package org.zero.common.data.format;

import cn.hutool.core.text.StrFormatter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.Map;

/**
 * 基于 Hutool {@link StrFormatter} 的命名参数消息格式化器。
 *
 * <p>使用 Hutool 的 {@link StrFormatter#format(CharSequence, Map, boolean)} 进行格式化，
 * 占位符语法为 {@code {key}}，通过 {@link Map} 的键进行命名匹配替换。</p>
 *
 * <h3>行为说明</h3>
 * <ul>
 *     <li>{@link #ignoreNull} 为 {@code true} 时，值为 {@code null} 的参数保留占位符原样输出</li>
 *     <li>{@link #ignoreNull} 为 {@code false} 时，{@code null} 值替换为字符串 {@code "null"}</li>
 * </ul>
 *
 * @author Zero (cnzeropro@163.com)
 * @see StrFormatter
 * @since 2026/03/27
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class HutoolMapMessageFormatter implements MapMessageFormatter {

	/**
	 * 默认单例，忽略 {@code null} 值（保留占位符原样输出）。
	 */
	public static final HutoolMapMessageFormatter INSTANCE = new HutoolMapMessageFormatter(true);

	/**
	 * 是否忽略 {@code null} 值参数。
	 *
	 * @see StrFormatter#format(CharSequence, Map, boolean)
	 */
	protected final boolean ignoreNull;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String format(CharSequence pattern, Locale locale, Map<?, ?> args) {
		return this.format(pattern, args, this.ignoreNull);
	}

	public String format(CharSequence template, Map<?, ?> map, boolean ignoreNull) {
		return StrFormatter.format(template, map, ignoreNull);
	}
}
