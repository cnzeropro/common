package org.zero.common.data.exception;

import org.zero.common.data.constant.StringPool;
import org.zero.common.data.enumeration.Status;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 聚合多个异常的运行时异常。
 *
 * <p>构造时会过滤 {@code null} 元素，并按输入顺序保留剩余异常。第一个异常会挂到标准
 * {@link #getCause()}，其余异常通过 {@link #getSuppressed()} 暴露给日志与监控系统。</p>
 *
 * <p>未显式提供 message，或 message 为空白时，会自动生成英文摘要消息。</p>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/18
 */
public class MultiException extends BaseException {
	/** 未提供显式消息时使用的默认摘要文本。 */
	private static final String DEFAULT_MESSAGE = "Multiple exceptions occurred";

	/**
	 * 过滤 {@code null} 后的异常数组，保持原始顺序。
	 */
	private final Throwable[] throwables;

	/**
	 * 使用聚合异常数组构造异常。
	 *
	 * @param throwables 待聚合的异常数组，可为 {@code null}
	 */
	public MultiException(Throwable... throwables) {
		this(null, throwables);
	}

	/**
	 * 使用显式消息与聚合异常数组构造异常。
	 *
	 * @param message    显式异常消息，为 {@code null} 或空白时回退到自动摘要
	 * @param throwables 待聚合的异常数组，可为 {@code null}
	 */
	public MultiException(String message, Throwable... throwables) {
		this(message, ResolvedThrowables.resolve(throwables));
	}

	private MultiException(String message, ResolvedThrowables resolvedThrowables) {
		super(resolveMessage(message, resolvedThrowables.throwables), resolvedThrowables.cause, Status.Default.ERROR);
		this.throwables = resolvedThrowables.throwables;
		this.registerSuppressed();
	}

	/**
	 * 解析最终使用的异常消息。
	 *
	 * @param message    显式消息，可为 {@code null}
	 * @param throwables 标准化后的异常数组，可为 {@code null}
	 * @return 解析后的消息字符串
	 */
	private static String resolveMessage(String message, Throwable[] throwables) {
		if (Objects.nonNull(message) && !message.trim().isEmpty()) {
			return message;
		}
		if (Objects.isNull(throwables) || throwables.length == 0) {
			return DEFAULT_MESSAGE;
		}
		return String.format(
			"%s (count=%d): %s",
			DEFAULT_MESSAGE,
			throwables.length,
			Arrays.stream(throwables)
				.map(Throwable::toString)
				.collect(Collectors.joining(StringPool.SEMICOLON + StringPool.SPACE))
		);
	}

	public Throwable[] getThrowables() {
		return Arrays.copyOf(this.throwables, this.throwables.length);
	}

	@Override
	public String getLocalizedMessage() {
		return this.getMessage();
	}

	@Override
	public CharSequence getErrorMessage() {
		return this.getMessage();
	}

	/**
	 * 将除 {@code cause}（即第一个异常）之外的其余异常按顺序注册到 {@code suppressed}。
	 */
	protected void registerSuppressed() {
		Throwable cause = this.getCause();
		for (int index = 1; index < this.throwables.length; index++) {
			Throwable throwable = this.throwables[index];
			if (Objects.nonNull(throwable) && throwable != cause) {
				this.addSuppressed(throwable);
			}
		}
	}

	private static final class ResolvedThrowables {
		private final Throwable[] throwables;
		private final Throwable cause;

		private ResolvedThrowables(Throwable[] throwables) {
			this.throwables = throwables;
			this.cause = throwables.length > 0 ? throwables[0] : null;
		}

		private static ResolvedThrowables resolve(Throwable[] throwables) {
			if (Objects.isNull(throwables) || throwables.length == 0) {
				return new ResolvedThrowables(new Throwable[0]);
			}
			return new ResolvedThrowables(Arrays.stream(throwables)
					.filter(Objects::nonNull)
					.toArray(Throwable[]::new));
		}
	}
}
