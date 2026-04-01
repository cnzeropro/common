package org.zero.common.data.exception;

import lombok.Getter;
import org.zero.common.data.constant.StringPool;

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
@Getter
public class MultiException extends BaseException {
	/** 未提供显式消息时使用的默认摘要文本。 */
	private static final String DEFAULT_MESSAGE = "Multiple exceptions occurred";

	/** 过滤 {@code null} 后的异常数组，保留原始输入顺序。 */
	protected final Throwable[] throwables;

	/**
	 * 使用聚合异常数组构造异常。
	 *
	 * <p>会过滤 {@code null} 元素；过滤后第一个异常作为 {@code cause}，
	 * 其余异常按顺序挂到 {@code suppressed}。message 自动生成。</p>
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
		super(resolveMessage(message, throwables), resolveCause(throwables));
		this.throwables = throwables;
		this.registerSuppressed();
	}

	/**
	 * 返回本地化消息，当前实现直接委托给 {@link #getMessage()}。
	 *
	 * @return 异常消息字符串
	 */
	@Override
	public String getLocalizedMessage() {
		return this.getMessage();
	}

	/**
	 * 将除 {@code cause}（即第一个异常）之外的其余异常按顺序注册到 {@code suppressed}。
	 *
	 * <p>跳过为 {@code null} 的元素以及与 {@code cause} 引用相同的元素，
	 * 避免重复注册。</p>
	 */
	protected void registerSuppressed() {
		Throwable cause = this.getCause();
		for (int index = 1; index < throwables.length; index++) {
			Throwable throwable = throwables[index];
			if (throwable != cause) {
				this.addSuppressed(throwable);
			}
		}
	}

	/**
	 * 从异常数组中提取第一个元素作为 {@code cause}。
	 *
	 * @param throwables 异常数组，可为 {@code null}
	 * @return 第一个异常，数组为空或为 {@code null} 时返回 {@code null}
	 */
	private static Throwable resolveCause(Throwable[] throwables) {
		return Objects.nonNull(throwables) && throwables.length > 0 ? throwables[0] : null;
	}

	/**
	 * 解析最终使用的异常消息。
	 *
	 * <p>优先使用显式 {@code message}；若为 {@code null} 则自动生成摘要，
	 * 格式为 {@code "Multiple exceptions occurred (count=N): ex1; ex2; ..."}。</p>
	 *
	 * @param message    显式消息，可为 {@code null}
	 * @param throwables 异常数组，可为 {@code null}
	 * @return 解析后的消息字符串
	 */
	private static String resolveMessage(String message, Throwable[] throwables) {
		if (Objects.nonNull(message)) {
			return message;
		}
		if (Objects.isNull(throwables) || throwables.length <= 0) {
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
}
