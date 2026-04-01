package org.zero.common.data.exception;

import lombok.Getter;
import org.zero.common.data.enumeration.Status;
import org.zero.common.data.format.MessageFormatter;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * 基础异常
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/6/20
 */
@Getter
public class BaseException extends TemplatedMessageException {

	/* ************************************************************** Fields ************************************************************** */

	/**
	 * 错误状态
	 */
	protected final Status status;

	/* ************************************************************** Native Constructors ************************************************************** */

	/**
	 * 构造一个默认错误状态的异常。
	 */
	public BaseException() {
		this(Status.Default.ERROR);
	}

	/**
	 * 使用指定的错误状态构造异常，消息取自 {@code status.getMessage()}。
	 *
	 * @param status 错误状态，为 {@code null} 时回退到 {@link Status.Default#ERROR}
	 */
	public BaseException(Status status) {
		this(Objects.toString(Objects.nonNull(status) ? status.getMessage() : Status.Default.ERROR_MESSAGE, null), status);
	}

	/**
	 * 使用指定的错误消息构造异常。
	 *
	 * @param message 错误消息
	 */
	public BaseException(String message) {
		this(message, Status.Default.ERROR);
	}

	/**
	 * 使用指定的错误消息和状态构造异常。
	 *
	 * @param message 错误消息
	 * @param status  错误状态，为 {@code null} 时回退到 {@link Status.Default#ERROR}
	 */
	public BaseException(String message, Status status) {
		super(message);
		this.status = Objects.nonNull(status) ? status : Status.Default.ERROR;
	}

	/**
	 * 使用指定的原因构造异常。
	 *
	 * @param cause 异常原因
	 */
	public BaseException(Throwable cause) {
		this(cause, Status.Default.ERROR);
	}

	/**
	 * 使用指定的原因和状态构造异常。
	 *
	 * @param cause  异常原因
	 * @param status 错误状态，为 {@code null} 时回退到 {@link Status.Default#ERROR}
	 */
	public BaseException(Throwable cause, Status status) {
		super(cause);
		this.status = Objects.nonNull(status) ? status : Status.Default.ERROR;
	}

	/**
	 * 使用指定的消息和原因构造异常。
	 *
	 * @param message 错误消息
	 * @param cause   异常原因
	 */
	public BaseException(String message, Throwable cause) {
		this(message, cause, Status.Default.ERROR);
	}

	/**
	 * 使用指定的消息、原因和状态构造异常。
	 *
	 * @param message 错误消息
	 * @param cause   异常原因
	 * @param status  错误状态，为 {@code null} 时回退到 {@link Status.Default#ERROR}
	 */
	public BaseException(String message, Throwable cause, Status status) {
		super(message, cause);
		this.status = Objects.nonNull(status) ? status : Status.Default.ERROR;
	}

	/**
	 * 使用指定参数构造异常（完整签名）。
	 *
	 * @param message            错误消息
	 * @param cause              异常原因
	 * @param enableSuppression  是否启用抑制
	 * @param writableStackTrace 是否可写堆栈轨迹
	 * @param status             错误状态，为 {@code null} 时回退到 {@link Status.Default#ERROR}
	 */
	protected BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Status status) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.status = Objects.nonNull(status) ? status : Status.Default.ERROR;
	}

	/* ************************************************************** Pattern Constructors ************************************************************** */

	/**
	 * 使用消息模板和参数构造异常。
	 *
	 * @param pattern 消息模板
	 * @param args    模板参数
	 */
	public BaseException(CharSequence pattern, Object... args) {
		this(Status.Default.ERROR, pattern, args);
	}

	/**
	 * 使用指定的消息模板、区域和参数构造异常。
	 *
	 * @param pattern 消息模板
	 * @param locale  区域设置
	 * @param args    模板参数
	 */
	public BaseException(CharSequence pattern, Locale locale, Object... args) {
		this(Status.Default.ERROR, pattern, locale, args);
	}

	/**
	 * 使用指定的状态、消息模板和参数构造异常。
	 *
	 * @param status  错误状态，为 {@code null} 时回退到 {@link Status.Default#ERROR}
	 * @param pattern 消息模板
	 * @param args    模板参数
	 */
	public BaseException(Status status, CharSequence pattern, Object... args) {
		this(status, (Throwable) null, pattern, args);
	}

	/**
	 * 使用指定的状态、消息模板、区域和参数构造异常。
	 *
	 * @param status  错误状态，为 {@code null} 时回退到 {@link Status.Default#ERROR}
	 * @param pattern 消息模板
	 * @param locale  区域设置
	 * @param args    模板参数
	 */
	public BaseException(Status status, CharSequence pattern, Locale locale, Object... args) {
		this(status, (Throwable) null, pattern, locale, args);
	}

	/**
	 * 使用指定的原因、消息模板和参数构造异常。
	 *
	 * @param cause   异常原因
	 * @param pattern 消息模板
	 * @param args    模板参数
	 */
	public BaseException(Throwable cause, CharSequence pattern, Object... args) {
		this(Status.Default.ERROR, cause, pattern, args);
	}

	/**
	 * 使用指定的原因、消息模板、区域和参数构造异常。
	 *
	 * @param cause   异常原因
	 * @param pattern 消息模板
	 * @param locale  区域设置
	 * @param args    模板参数
	 */
	public BaseException(Throwable cause, CharSequence pattern, Locale locale, Object... args) {
		this(Status.Default.ERROR, cause, pattern, locale, args);
	}

	/**
	 * 使用指定的状态、原因、消息模板和参数构造异常。
	 *
	 * @param status  错误状态，为 {@code null} 时回退到 {@link Status.Default#ERROR}
	 * @param cause   异常原因
	 * @param pattern 消息模板
	 * @param args    模板参数
	 */
	public BaseException(Status status, Throwable cause, CharSequence pattern, Object... args) {
		this(status, cause, null, pattern, args);
	}

	/**
	 * 使用指定的状态、原因、消息模板、区域和参数构造异常。
	 *
	 * @param status  错误状态，为 {@code null} 时回退到 {@link Status.Default#ERROR}
	 * @param cause   异常原因
	 * @param pattern 消息模板
	 * @param locale  区域设置
	 * @param args    模板参数
	 */
	public BaseException(Status status, Throwable cause, CharSequence pattern, Locale locale, Object... args) {
		this(status, cause, null, pattern, locale, args);
	}

	/* ************************************************************** Formatter Constructors ************************************************************** */

	/**
	 * 使用指定的格式化器、消息模板和参数构造异常。
	 *
	 * @param formatter 消息格式化器
	 * @param pattern   消息模板
	 * @param args      模板参数
	 */
	public BaseException(MessageFormatter formatter, CharSequence pattern, Object... args) {
		this(Status.Default.ERROR, formatter, pattern, args);
	}

	/**
	 * 使用指定的格式化器、消息模板、区域和参数构造异常。
	 *
	 * @param formatter 消息格式化器
	 * @param pattern   消息模板
	 * @param locale    区域设置
	 * @param args      模板参数
	 */
	public BaseException(MessageFormatter formatter, CharSequence pattern, Locale locale, Object... args) {
		this(Status.Default.ERROR, formatter, pattern, locale, args);
	}

	/**
	 * 使用指定的状态、格式化器、消息模板和参数构造异常。
	 *
	 * @param status    错误状态，为 {@code null} 时回退到 {@link Status.Default#ERROR}
	 * @param formatter 消息格式化器
	 * @param pattern   消息模板
	 * @param args      模板参数
	 */
	public BaseException(Status status, MessageFormatter formatter, CharSequence pattern, Object... args) {
		this(status, null, formatter, pattern, args);
	}

	/**
	 * 使用指定的状态、格式化器、消息模板、区域和参数构造异常。
	 *
	 * @param status    错误状态，为 {@code null} 时回退到 {@link Status.Default#ERROR}
	 * @param formatter 消息格式化器
	 * @param pattern   消息模板
	 * @param locale    区域设置
	 * @param args      模板参数
	 */
	public BaseException(Status status, MessageFormatter formatter, CharSequence pattern, Locale locale, Object... args) {
		this(status, null, formatter, pattern, locale, args);
	}

	/**
	 * 使用指定的原因、格式化器、消息模板和参数构造异常。
	 *
	 * @param cause     异常原因
	 * @param formatter 消息格式化器
	 * @param pattern   消息模板
	 * @param args      模板参数
	 */
	public BaseException(Throwable cause, MessageFormatter formatter, CharSequence pattern, Object... args) {
		this(Status.Default.ERROR, cause, formatter, pattern, args);
	}

	/**
	 * 使用指定的原因、格式化器、消息模板、区域和参数构造异常。
	 *
	 * @param cause     异常原因
	 * @param formatter 消息格式化器
	 * @param pattern   消息模板
	 * @param locale    区域设置
	 * @param args      模板参数
	 */
	public BaseException(Throwable cause, MessageFormatter formatter, CharSequence pattern, Locale locale, Object... args) {
		this(Status.Default.ERROR, cause, formatter, pattern, locale, args);
	}

	/**
	 * 使用指定的状态、原因、格式化器、消息模板和参数构造异常。
	 *
	 * @param status    错误状态，为 {@code null} 时回退到 {@link Status.Default#ERROR}
	 * @param cause     异常原因
	 * @param formatter 消息格式化器
	 * @param pattern   消息模板
	 * @param args      模板参数
	 */
	public BaseException(Status status, Throwable cause, MessageFormatter formatter, CharSequence pattern, Object... args) {
		this(status, cause, formatter, pattern, null, args);
	}

	/**
	 * 使用指定的状态、原因、格式化器、消息模板、区域和参数构造异常（完整模板签名）。
	 *
	 * @param status    错误状态，为 {@code null} 时回退到 {@link Status.Default#ERROR}
	 * @param cause     异常原因
	 * @param formatter 消息格式化器
	 * @param pattern   消息模板
	 * @param locale    区域设置
	 * @param args      模板参数
	 */
	public BaseException(Status status, Throwable cause, MessageFormatter formatter, CharSequence pattern, Locale locale, Object... args) {
		super(cause, formatter, pattern, locale, args);
		this.status = Objects.nonNull(status) ? status : Status.Default.ERROR;
	}

	/* ************************************************************** Tuple Constructors ************************************************************** */

	/**
	 * 使用参数元组构造异常。
	 *
	 * @param paramTuple 模板消息参数元组
	 */
	public BaseException(TemplatedMessageException.ParamTuple paramTuple) {
		this(Status.Default.ERROR, paramTuple);
	}

	/**
	 * 使用指定的状态和参数元组构造异常。
	 *
	 * @param status     错误状态，为 {@code null} 时回退到 {@link Status.Default#ERROR}
	 * @param paramTuple 模板消息参数元组
	 */
	public BaseException(Status status, TemplatedMessageException.ParamTuple paramTuple) {
		super(paramTuple);
		this.status = Objects.nonNull(status) ? status : Status.Default.ERROR;
	}

	/**
	 * 使用指定的状态和参数元组构造异常（完整签名）。
	 *
	 * @param status             错误状态，为 {@code null} 时回退到 {@link Status.Default#ERROR}
	 * @param paramTuple         模板消息参数元组
	 * @param enableSuppression  是否启用抑制
	 * @param writableStackTrace 是否可写堆栈轨迹
	 */
	protected BaseException(Status status, TemplatedMessageException.ParamTuple paramTuple, boolean enableSuppression, boolean writableStackTrace) {
		super(paramTuple, enableSuppression, writableStackTrace);
		this.status = Objects.nonNull(status) ? status : Status.Default.ERROR;
	}

	/* ************************************************************** Accessors ************************************************************** */

	/**
	 * 获取错误码。
	 *
	 * @return 错误码
	 */
	public Serializable getErrorCode() {
		return status.getCode();
	}

	/**
	 * 获取错误消息。
	 *
	 * @return 错误消息
	 */
	public CharSequence getErrorMessage() {
		return status.getMessage();
	}

	/**
	 * 获取本地化错误消息。
	 *
	 * @return 本地化错误消息
	 */
	@Override
	public String getLocalizedMessage() {
		return Objects.toString(this.getErrorMessage(), null);
	}
}
