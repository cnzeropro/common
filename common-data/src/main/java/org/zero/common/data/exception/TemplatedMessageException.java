package org.zero.common.data.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.zero.common.data.format.DefaultMessageFormatter;
import org.zero.common.data.format.MessageFormatter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * 支持模板消息的基础异常。
 *
 * <p>在标准 {@link RuntimeException} 基础上，扩展了模板消息格式化能力：
 * 通过 {@link MessageFormatter} 对 {@code pattern} 进行参数化格式化，
 * 生成最终异常消息。</p>
 *
 * <h3>构造方式</h3>
 * <ul>
 *     <li><b>Native</b> — 与 {@link RuntimeException} 完全一致的 5 种标准签名</li>
 *     <li><b>Extended</b> — 基于 {@code pattern + args} 的模板构造，
 *         支持可选的 {@code cause}、{@code formatter}、{@code locale}</li>
 *     <li><b>Tuple</b> — 通过 {@link ParamTuple} 或 {@link ResolvedTuple} 一次性传入所有参数</li>
 * </ul>
 *
 * <h3>Cause 解析规则</h3>
 * <ol>
 *     <li>若显式传入了 {@code cause}，直接使用</li>
 *     <li>若 {@code cause} 为 {@code null}，且 {@code args} 尾参为 {@link Throwable}，
 *         则自动提取该尾参作为 cause，并将其从格式化参数中移除</li>
 * </ol>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/26
 * @see MessageFormatter
 * @see DefaultMessageFormatter
 * @see ParamTuple
 */
public class TemplatedMessageException extends RuntimeException {

	/* ************************************************************** Native ************************************************************** */

	/**
	 * 无参构造。
	 */
	public TemplatedMessageException() {
		super();
	}

	/**
	 * 指定异常消息。
	 *
	 * @param message 异常详情
	 */
	public TemplatedMessageException(String message) {
		super(message);
	}

	/**
	 * 指定原因异常。
	 *
	 * @param cause 原因异常
	 */
	public TemplatedMessageException(Throwable cause) {
		super(cause);
	}

	/**
	 * 指定异常消息和原因异常。
	 *
	 * @param message 异常详情
	 * @param cause   原因异常
	 */
	public TemplatedMessageException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 完整签名的原生构造。
	 *
	 * @param message            异常详情
	 * @param cause              原因异常
	 * @param enableSuppression  是否启用抑制
	 * @param writableStackTrace 是否可写堆栈
	 */
	protected TemplatedMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/* ************************************************************** Extended ************************************************************** */

	/**
	 * 模板构造：使用默认格式化器、默认 Locale 格式化消息。
	 *
	 * @param pattern 消息模板
	 * @param args    格式化参数；若尾参为 {@link Throwable} 则自动提取为 cause
	 */
	public TemplatedMessageException(CharSequence pattern, Object... args) {
		this((Throwable) null, pattern, args);
	}

	/**
	 * 模板构造：指定显式 cause、使用默认格式化器、默认 Locale。
	 *
	 * @param cause   原因异常（显式指定，不从 args 中提取）
	 * @param pattern 消息模板
	 * @param args    格式化参数
	 */
	public TemplatedMessageException(Throwable cause, CharSequence pattern, Object... args) {
		this(cause, pattern, null, args);
	}

	/**
	 * 模板构造：指定 Locale、使用默认格式化器。
	 *
	 * @param pattern 消息模板
	 * @param locale  格式化 Locale，为 {@code null} 时使用默认值
	 * @param args    格式化参数；若尾参为 {@link Throwable} 则自动提取为 cause
	 */
	public TemplatedMessageException(CharSequence pattern, Locale locale, Object... args) {
		this((Throwable) null, pattern, locale, args);
	}

	/**
	 * 模板构造：指定显式 cause 和 Locale、使用默认格式化器。
	 *
	 * @param cause   原因异常（显式指定，不从 args 中提取）
	 * @param pattern 消息模板
	 * @param locale  格式化 Locale，为 {@code null} 时使用默认值
	 * @param args    格式化参数
	 */
	public TemplatedMessageException(Throwable cause, CharSequence pattern, Locale locale, Object... args) {
		this(cause, null, pattern, locale, args);
	}

	/**
	 * 模板构造：指定格式化器、使用默认 Locale。
	 *
	 * @param formatter 消息格式化器，为 {@code null} 时使用 {@link DefaultMessageFormatter#INSTANCE}
	 * @param pattern   消息模板
	 * @param args      格式化参数；若尾参为 {@link Throwable} 则自动提取为 cause
	 */
	public TemplatedMessageException(MessageFormatter formatter, CharSequence pattern, Object... args) {
		this(null, formatter, pattern, args);
	}

	/**
	 * 模板构造：指定显式 cause 和格式化器、使用默认 Locale。
	 *
	 * @param cause     原因异常（显式指定，不从 args 中提取）
	 * @param formatter 消息格式化器，为 {@code null} 时使用 {@link DefaultMessageFormatter#INSTANCE}
	 * @param pattern   消息模板
	 * @param args      格式化参数
	 */
	public TemplatedMessageException(Throwable cause, MessageFormatter formatter, CharSequence pattern, Object... args) {
		this(cause, formatter, pattern, null, args);
	}

	/**
	 * 模板构造：指定格式化器和 Locale。
	 *
	 * @param formatter 消息格式化器，为 {@code null} 时使用 {@link DefaultMessageFormatter#INSTANCE}
	 * @param pattern   消息模板
	 * @param locale    格式化 Locale，为 {@code null} 时使用默认值
	 * @param args      格式化参数；若尾参为 {@link Throwable} 则自动提取为 cause
	 */
	public TemplatedMessageException(MessageFormatter formatter, CharSequence pattern, Locale locale, Object... args) {
		this(null, formatter, pattern, locale, args);
	}

	/**
	 * 模板构造：完整参数版本，所有 Extended 构造器最终委托至此。
	 *
	 * @param cause     原因异常（显式指定，不从 args 中提取）
	 * @param formatter 消息格式化器
	 * @param pattern   消息模板
	 * @param locale    格式化 Locale
	 * @param args      格式化参数
	 */
	public TemplatedMessageException(Throwable cause, MessageFormatter formatter, CharSequence pattern, Locale locale, Object... args) {
		this(new ParamTuple(cause, formatter, pattern, locale, args));
	}

	/**
	 * 元组构造：通过 {@link ParamTuple} 一次性传入所有参数。
	 *
	 * @param paramTuple 参数元组，为 {@code null} 时使用默认值
	 */
	public TemplatedMessageException(ParamTuple paramTuple) {
		this(resolve(paramTuple));
	}

	/**
	 * 已解析构造：直接使用解析后的消息和 cause。
	 *
	 * @param resolvedTuple 已解析的参数元组
	 */
	protected TemplatedMessageException(ResolvedTuple resolvedTuple) {
		super(resolvedTuple.message, resolvedTuple.cause);
	}

	/**
	 * 元组构造（完整签名）：支持控制 suppression 和堆栈写入。
	 *
	 * @param paramTuple        参数元组
	 * @param enableSuppression 是否启用抑制
	 * @param writableStackTrace 是否可写堆栈
	 */
	protected TemplatedMessageException(ParamTuple paramTuple, boolean enableSuppression, boolean writableStackTrace) {
		this(resolve(paramTuple), enableSuppression, writableStackTrace);
	}

	/**
	 * 已解析构造（完整签名）：支持控制 suppression 和堆栈写入。
	 *
	 * @param resolvedTuple     已解析的参数元组
	 * @param enableSuppression 是否启用抑制
	 * @param writableStackTrace 是否可写堆栈
	 */
	protected TemplatedMessageException(ResolvedTuple resolvedTuple, boolean enableSuppression, boolean writableStackTrace) {
		super(resolvedTuple.message, resolvedTuple.cause, enableSuppression, writableStackTrace);
	}

	/* ************************************************************** Resolve ************************************************************** */

	/**
	 * 将 {@link ParamTuple} 解析为 {@link ResolvedTuple}。
	 *
	 * <p>解析逻辑：</p>
	 * <ol>
	 *     <li>空安全处理：{@code null} 参数使用默认值</li>
	 *     <li>Cause 解析：显式 cause 优先，否则从 args 尾参提取 {@link Throwable}</li>
	 *     <li>消息解析：若 pattern 非空则格式化，否则 fallback 到 cause.getMessage()</li>
	 * </ol>
	 *
	 * @param paramTuple 参数元组，为 {@code null} 时使用默认值
	 * @return 已解析的 {@link ResolvedTuple}
	 */
	protected static ResolvedTuple resolve(ParamTuple paramTuple) {
		ParamTuple actualParamTuple = Objects.nonNull(paramTuple) ? paramTuple : new ParamTuple();
		Object[] sourceArgs = Objects.nonNull(actualParamTuple.args) ? actualParamTuple.args : new Object[0];
		Throwable actualCause = actualParamTuple.cause;
		Object[] actualArgs = sourceArgs;
		if (Objects.isNull(actualCause)) {
			// 模板构造里仅约定尾参 Throwable 为隐式 cause，并且不参与消息格式化。
			actualCause = getTrailingThrowable(sourceArgs);
			if (Objects.nonNull(actualCause)) {
				actualArgs = removeTrailingThrowable(sourceArgs);
			}
		}
		MessageFormatter actualFormatter = Objects.nonNull(actualParamTuple.formatter) ? actualParamTuple.formatter : DefaultMessageFormatter.INSTANCE;
		String message = null;
		if (Objects.nonNull(actualParamTuple.pattern)) {
			message = actualFormatter.format(actualParamTuple.pattern, actualParamTuple.locale, actualArgs);
		} else if (Objects.nonNull(actualCause)) {
			message = actualCause.getMessage();
		}

		return new ResolvedTuple(message, actualCause);
	}

	/**
	 * 从参数数组末尾提取 {@link Throwable} 候选。
	 *
	 * @param args 参数数组
	 * @return 尾参的 {@link Throwable}，不存在或为空时返回 {@code null}
	 */
	protected static Throwable getTrailingThrowable(Object[] args) {
		if (Objects.isNull(args) || args.length <= 0) {
			return null;
		}
		Object last = args[args.length - 1];
		return last instanceof Throwable ? (Throwable) last : null;
	}

	/**
	 * 返回去掉最后一个元素的数组副本。
	 *
	 * <p>用于在尾参 {@link Throwable} 被提取为 cause 后，
	 * 从格式化参数中将其移除。</p>
	 *
	 * @param args 原始参数数组
	 * @return 去掉尾元素的新数组；若输入为空或单元素则返回空数组
	 */
	protected static Object[] removeTrailingThrowable(Object[] args) {
		if (Objects.isNull(args) || args.length <= 0) {
			return args;
		}
		return Arrays.copyOf(args, args.length - 1);
	}

	/* ************************************************************** Inner Classes ************************************************************** */

	/**
	 * 已解析的参数元组，持有最终生效的 {@code message} 和 {@code cause}。
	 *
	 * <p>引入本类的根本原因在于 Java 语言规范要求构造器显式委托语句
	 * （{@code this(...)} 或 {@code super(...)}）必须作为构造体首行出现，
	 * 因此无法在委托前内联执行 {@link #resolve(ParamTuple)} 等预处理逻辑。
	 * 本类将解析结果物化为不可变结构，以绕过上述限制。</p>
	 *
	 * <p>自 Java 22 起（JEP 447: Statements before super()），
	 * 允许在显式委托前执行不引用 {@code this} 的语句，
	 * 后续版本可考虑移除此中间类并改为构造器内直接解析。</p>
	 */
	@RequiredArgsConstructor
	protected static class ResolvedTuple {
		protected final String message;
		protected final Throwable cause;
	}

	/**
	 * 原始参数元组，作为模板构造器的统一入参。
	 *
	 * <p>支持通过 {@link Builder} 或全参构造器创建，字段均可为 {@code null}
	 * 以使用默认值。</p>
	 *
	 * <h4>Cause 解析优先级</h4>
	 * <ol>
	 *     <li>显式 {@link #cause} 优先级最高</li>
	 *     <li>{@link #cause} 为空时，尝试从 {@link #args} 尾参提取 {@link Throwable}</li>
	 * </ol>
	 */
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder(toBuilder = true)
	public static class ParamTuple {
		/**
		 * 显式 cause 优先级最高；为空时才尝试从 {@link #args} 尾参提取。
		 */
		protected Throwable cause;

		/**
		 * 消息格式化器，为 {@code null} 时使用 {@link DefaultMessageFormatter#INSTANCE}。
		 */
		@Builder.Default
		protected MessageFormatter formatter = DefaultMessageFormatter.INSTANCE;

		/**
		 * 消息模板模式，为 {@code null} 时 fallback 到 cause.getMessage()。
		 */
		protected CharSequence pattern;

		/**
		 * 格式化 Locale，为 {@code null} 时使用默认值。
		 */
		protected Locale locale;

		/**
		 * 格式化参数；若未显式指定 cause 且尾参为 {@link Throwable}，
		 * 则自动提取为 cause 并从本数组中移除。
		 */
		@Builder.Default
		protected Object[] args = {};
	}
}
