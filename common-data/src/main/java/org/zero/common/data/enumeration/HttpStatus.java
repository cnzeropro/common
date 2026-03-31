package org.zero.common.data.enumeration;

import lombok.RequiredArgsConstructor;
import org.zero.common.data.constant.StringPool;

/**
 * HTTP 状态码枚举。
 * <p>
 * 规范名称以当前 IANA HTTP Status Code Registry 与现行 RFC 为准。
 * 为兼容历史实现，本枚举保留了历史别名、已废弃状态码以及非标准扩展状态码；
 * 其中不再建议直接使用的定义统一标注 {@link Deprecated}。
 *
 * @author Zero (cnzeropro@163.com)
 * @see Series
 * @see <a href="https://www.iana.org/assignments/http-status-codes/http-status-codes.xhtml">HTTP Status Code Registry</a>
 * @since 2018/7/1
 */
@RequiredArgsConstructor
public enum HttpStatus {
	/* ********************************************************************************** 1xx 信息响应 ********************************************************************************** */
	/**
	 * 100 Continue，请求头已被接收，客户端可以继续发送请求体。
	 */
	CONTINUE(org.zero.common.data.constant.HttpStatus.CONTINUE, Series.INFORMATIONAL, "Continue"),
	/**
	 * 101 Switching Protocols，服务器将根据 Upgrade 头切换协议。
	 */
	SWITCHING_PROTOCOLS(org.zero.common.data.constant.HttpStatus.SWITCHING_PROTOCOLS, Series.INFORMATIONAL, "Switching Protocols"),
	/**
	 * 102 Processing，表示服务器正在处理请求但尚未完成最终响应。
	 *
	 * @deprecated IANA 仍保留该注册项，但 RFC 4918 已移除其定义，建议仅为兼容历史实现保留。
	 */
	@Deprecated
	PROCESSING(org.zero.common.data.constant.HttpStatus.PROCESSING, Series.INFORMATIONAL, "Processing"),
	/**
	 * 103 Early Hints，在最终响应前提前返回提示头，便于客户端预加载资源。
	 */
	EARLY_HINTS(org.zero.common.data.constant.HttpStatus.EARLY_HINTS, Series.INFORMATIONAL, "Early Hints"),
	/**
	 * 103 Checkpoint，非标准扩展中对 103 的历史命名。
	 *
	 * @deprecated 非标准历史别名，请使用 {@link #EARLY_HINTS}。
	 */
	@Deprecated
	CHECKPOINT(org.zero.common.data.constant.HttpStatus.CHECKPOINT, Series.INFORMATIONAL, "Checkpoint"),

	/* ********************************************************************************** 2xx 成功响应 ********************************************************************************** */
	/**
	 * 200 OK，请求已成功处理。
	 */
	OK(org.zero.common.data.constant.HttpStatus.OK, Series.SUCCESSFUL, "OK"),
	/**
	 * 201 Created，请求成功并创建了新的资源。
	 */
	CREATED(org.zero.common.data.constant.HttpStatus.CREATED, Series.SUCCESSFUL, "Created"),
	/**
	 * 202 Accepted，请求已被接受，但尚未完成处理。
	 */
	ACCEPTED(org.zero.common.data.constant.HttpStatus.ACCEPTED, Series.SUCCESSFUL, "Accepted"),
	/**
	 * 203 Non-Authoritative Information，返回的是副本或转换后的非权威信息。
	 */
	NON_AUTHORITATIVE_INFORMATION(
		org.zero.common.data.constant.HttpStatus.NON_AUTHORITATIVE_INFORMATION,
		Series.SUCCESSFUL,
		"Non-Authoritative Information"
	),
	/**
	 * 204 No Content，请求成功，但响应中没有消息体。
	 */
	NO_CONTENT(org.zero.common.data.constant.HttpStatus.NO_CONTENT, Series.SUCCESSFUL, "No Content"),
	/**
	 * 205 Reset Content，请求成功，客户端应重置当前视图或表单状态。
	 */
	RESET_CONTENT(org.zero.common.data.constant.HttpStatus.RESET_CONTENT, Series.SUCCESSFUL, "Reset Content"),
	/**
	 * 206 Partial Content，返回资源的部分内容，常用于范围请求。
	 */
	PARTIAL_CONTENT(org.zero.common.data.constant.HttpStatus.PARTIAL_CONTENT, Series.SUCCESSFUL, "Partial Content"),
	/**
	 * 207 Multi-Status，WebDAV 多状态响应，可在响应体中携带多个子结果。
	 */
	MULTI_STATUS(org.zero.common.data.constant.HttpStatus.MULTI_STATUS, Series.SUCCESSFUL, "Multi-Status"),
	/**
	 * 208 Already Reported，WebDAV 中表示同一绑定资源已在前文报告过。
	 */
	ALREADY_REPORTED(org.zero.common.data.constant.HttpStatus.ALREADY_REPORTED, Series.SUCCESSFUL, "Already Reported"),
	/**
	 * 226 IM Used，表示服务器已对资源应用实例操作后返回结果。
	 */
	IM_USED(org.zero.common.data.constant.HttpStatus.IM_USED, Series.SUCCESSFUL, "IM Used"),

	/* ********************************************************************************** 3xx 重定向 ********************************************************************************** */
	/**
	 * 300 Multiple Choices，目标资源存在多个可供选择的表示。
	 */
	MULTIPLE_CHOICES(org.zero.common.data.constant.HttpStatus.MULTIPLE_CHOICES, Series.REDIRECTION, "Multiple Choices"),
	/**
	 * 301 Moved Permanently，目标资源已被永久移动到新的 URI。
	 */
	MOVED_PERMANENTLY(org.zero.common.data.constant.HttpStatus.MOVED_PERMANENTLY, Series.REDIRECTION, "Moved Permanently"),
	/**
	 * 302 Found，目标资源临时位于其他 URI。
	 */
	FOUND(org.zero.common.data.constant.HttpStatus.FOUND, Series.REDIRECTION, "Found"),
	/**
	 * 302 Moved Temporarily，302 的历史命名。
	 *
	 * @deprecated 历史别名，请使用 {@link #FOUND}。
	 */
	@Deprecated
	MOVED_TEMPORARILY(org.zero.common.data.constant.HttpStatus.MOVED_TEMPORARILY, Series.REDIRECTION, "Moved Temporarily"),
	/**
	 * 303 See Other，建议客户端改用 GET 到其他 URI 获取结果。
	 */
	SEE_OTHER(org.zero.common.data.constant.HttpStatus.SEE_OTHER, Series.REDIRECTION, "See Other"),
	/**
	 * 304 Not Modified，资源未发生变化，客户端可继续使用缓存副本。
	 */
	NOT_MODIFIED(org.zero.common.data.constant.HttpStatus.NOT_MODIFIED, Series.REDIRECTION, "Not Modified"),
	/**
	 * 305 Use Proxy，要求通过代理访问目标资源。
	 *
	 * @deprecated 已被 RFC 9110 废弃，不建议继续使用。
	 */
	@Deprecated
	USE_PROXY(org.zero.common.data.constant.HttpStatus.USE_PROXY, Series.REDIRECTION, "Use Proxy"),
	/**
	 * 306 (Unused)，保留且未使用。
	 *
	 * @deprecated 在 RFC 9110 中仍为保留未使用状态码。
	 */
	@Deprecated
	UNUSED_306(org.zero.common.data.constant.HttpStatus.UNUSED_306, Series.REDIRECTION, "(Unused)"),
	/**
	 * 307 Temporary Redirect，临时重定向且应保持原请求方法与请求体语义。
	 */
	TEMPORARY_REDIRECT(org.zero.common.data.constant.HttpStatus.TEMPORARY_REDIRECT, Series.REDIRECTION, "Temporary Redirect"),
	/**
	 * 308 Permanent Redirect，永久重定向且应保持原请求方法与请求体语义。
	 */
	PERMANENT_REDIRECT(org.zero.common.data.constant.HttpStatus.PERMANENT_REDIRECT, Series.REDIRECTION, "Permanent Redirect"),

	/* ********************************************************************************** 4xx 客户端错误 ********************************************************************************** */
	/**
	 * 400 Bad Request，请求语法或参数不符合服务器要求。
	 */
	BAD_REQUEST(org.zero.common.data.constant.HttpStatus.BAD_REQUEST, Series.CLIENT_ERROR, "Bad Request"),
	/**
	 * 401 Unauthorized，当前请求需要通过身份认证。
	 */
	UNAUTHORIZED(org.zero.common.data.constant.HttpStatus.UNAUTHORIZED, Series.CLIENT_ERROR, "Unauthorized"),
	/**
	 * 402 Payment Required，保留用于未来可能的支付相关场景。
	 */
	PAYMENT_REQUIRED(org.zero.common.data.constant.HttpStatus.PAYMENT_REQUIRED, Series.CLIENT_ERROR, "Payment Required"),
	/**
	 * 403 Forbidden，服务器理解请求但拒绝执行。
	 */
	FORBIDDEN(org.zero.common.data.constant.HttpStatus.FORBIDDEN, Series.CLIENT_ERROR, "Forbidden"),
	/**
	 * 404 Not Found，目标资源不存在。
	 */
	NOT_FOUND(org.zero.common.data.constant.HttpStatus.NOT_FOUND, Series.CLIENT_ERROR, "Not Found"),
	/**
	 * 405 Method Not Allowed，请求方法不被目标资源允许。
	 */
	METHOD_NOT_ALLOWED(org.zero.common.data.constant.HttpStatus.METHOD_NOT_ALLOWED, Series.CLIENT_ERROR, "Method Not Allowed"),
	/**
	 * 406 Not Acceptable，服务器无法提供满足内容协商条件的表示。
	 */
	NOT_ACCEPTABLE(org.zero.common.data.constant.HttpStatus.NOT_ACCEPTABLE, Series.CLIENT_ERROR, "Not Acceptable"),
	/**
	 * 407 Proxy Authentication Required，请求方需要先通过代理认证。
	 */
	PROXY_AUTHENTICATION_REQUIRED(
		org.zero.common.data.constant.HttpStatus.PROXY_AUTHENTICATION_REQUIRED,
		Series.CLIENT_ERROR,
		"Proxy Authentication Required"
	),
	/**
	 * 408 Request Timeout，服务器等待请求超时。
	 */
	REQUEST_TIMEOUT(org.zero.common.data.constant.HttpStatus.REQUEST_TIMEOUT, Series.CLIENT_ERROR, "Request Timeout"),
	/**
	 * 409 Conflict，请求与目标资源当前状态发生冲突。
	 */
	CONFLICT(org.zero.common.data.constant.HttpStatus.CONFLICT, Series.CLIENT_ERROR, "Conflict"),
	/**
	 * 410 Gone，目标资源已永久移除且预期不会恢复。
	 */
	GONE(org.zero.common.data.constant.HttpStatus.GONE, Series.CLIENT_ERROR, "Gone"),
	/**
	 * 411 Length Required，请求缺少必需的内容长度信息。
	 */
	LENGTH_REQUIRED(org.zero.common.data.constant.HttpStatus.LENGTH_REQUIRED, Series.CLIENT_ERROR, "Length Required"),
	/**
	 * 412 Precondition Failed，请求中的前置条件未满足。
	 */
	PRECONDITION_FAILED(org.zero.common.data.constant.HttpStatus.PRECONDITION_FAILED, Series.CLIENT_ERROR, "Precondition Failed"),
	/**
	 * 413 Content Too Large，请求内容过大。
	 * 这是 RFC 9110 中的当前规范名称。
	 */
	CONTENT_TOO_LARGE(org.zero.common.data.constant.HttpStatus.CONTENT_TOO_LARGE, Series.CLIENT_ERROR, "Content Too Large"),
	/**
	 * 413 Payload Too Large，413 的旧规范名称。
	 *
	 * @deprecated 在 RFC 9110 中已更名为 {@link #CONTENT_TOO_LARGE}。
	 */
	@Deprecated
	PAYLOAD_TOO_LARGE(org.zero.common.data.constant.HttpStatus.PAYLOAD_TOO_LARGE, Series.CLIENT_ERROR, "Payload Too Large"),
	/**
	 * 413 Request Entity Too Large，413 的历史命名。
	 *
	 * @deprecated 历史别名，请使用 {@link #CONTENT_TOO_LARGE}。
	 */
	@Deprecated
	REQUEST_ENTITY_TOO_LARGE(
		org.zero.common.data.constant.HttpStatus.REQUEST_ENTITY_TOO_LARGE,
		Series.CLIENT_ERROR,
		"Request Entity Too Large"
	),
	/**
	 * 414 URI Too Long，请求目标 URI 过长。
	 */
	URI_TOO_LONG(org.zero.common.data.constant.HttpStatus.URI_TOO_LONG, Series.CLIENT_ERROR, "URI Too Long"),
	/**
	 * 414 Request-URI Too Long，414 的历史命名。
	 *
	 * @deprecated 历史别名，请使用 {@link #URI_TOO_LONG}。
	 */
	@Deprecated
	REQUEST_URI_TOO_LONG(org.zero.common.data.constant.HttpStatus.REQUEST_URI_TOO_LONG, Series.CLIENT_ERROR, "Request-URI Too Long"),
	/**
	 * 415 Unsupported Media Type，请求消息体的媒体类型不受支持。
	 */
	UNSUPPORTED_MEDIA_TYPE(org.zero.common.data.constant.HttpStatus.UNSUPPORTED_MEDIA_TYPE, Series.CLIENT_ERROR, "Unsupported Media Type"),
	/**
	 * 416 Range Not Satisfiable，请求的范围无法满足。
	 * 这是 RFC 9110 中的当前规范名称。
	 */
	RANGE_NOT_SATISFIABLE(org.zero.common.data.constant.HttpStatus.RANGE_NOT_SATISFIABLE, Series.CLIENT_ERROR, "Range Not Satisfiable"),
	/**
	 * 416 Requested Range Not Satisfiable，416 的旧规范名称。
	 *
	 * @deprecated 在 RFC 9110 中已更名为 {@link #RANGE_NOT_SATISFIABLE}。
	 */
	@Deprecated
	REQUESTED_RANGE_NOT_SATISFIABLE(
		org.zero.common.data.constant.HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,
		Series.CLIENT_ERROR,
		"Requested Range Not Satisfiable"
	),
	/**
	 * 417 Expectation Failed，服务器无法满足 Expect 请求头中的期望。
	 */
	EXPECTATION_FAILED(org.zero.common.data.constant.HttpStatus.EXPECTATION_FAILED, Series.CLIENT_ERROR, "Expectation Failed"),
	/**
	 * 418 (Unused)，当前规范将该状态码保留且未使用。
	 *
	 * @deprecated 在 RFC 9110 中被保留为未使用状态码。
	 */
	@Deprecated
	UNUSED_418(org.zero.common.data.constant.HttpStatus.UNUSED_418, Series.CLIENT_ERROR, "(Unused)"),
	/**
	 * 418 I'm a teapot，历史上的玩笑性质扩展状态码。
	 *
	 * @deprecated 非标准历史扩展；现行 RFC 已将 418 保留为未使用状态码。
	 */
	@Deprecated
	I_AM_A_TEAPOT(org.zero.common.data.constant.HttpStatus.I_AM_A_TEAPOT, Series.CLIENT_ERROR, "I'm a teapot"),
	/**
	 * 419 Insufficient Space on Resource，表示目标资源可用空间不足。
	 *
	 * @deprecated 非标准历史 WebDAV 扩展状态码。
	 */
	@Deprecated
	INSUFFICIENT_SPACE_ON_RESOURCE(
		org.zero.common.data.constant.HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE,
		Series.CLIENT_ERROR,
		"Insufficient Space On Resource"
	),
	/**
	 * 420 Method Failure，表示方法执行失败。
	 *
	 * @deprecated 非标准历史 WebDAV 扩展状态码。
	 */
	@Deprecated
	METHOD_FAILURE(org.zero.common.data.constant.HttpStatus.METHOD_FAILURE, Series.CLIENT_ERROR, "Method Failure"),
	/**
	 * 421 Misdirected Request，请求被发送到了无法生成目标响应的服务器。
	 */
	MISDIRECTED_REQUEST(org.zero.common.data.constant.HttpStatus.MISDIRECTED_REQUEST, Series.CLIENT_ERROR, "Misdirected Request"),
	/**
	 * 421 Destination Locked，历史上与 421 共享数值的非标准命名。
	 *
	 * @deprecated 非标准历史别名，请使用 {@link #MISDIRECTED_REQUEST}。
	 */
	@Deprecated
	DESTINATION_LOCKED(org.zero.common.data.constant.HttpStatus.DESTINATION_LOCKED, Series.CLIENT_ERROR, "Destination Locked"),
	/**
	 * 422 Unprocessable Content，请求内容语法正确，但语义上无法处理。
	 * 这是 RFC 9110 中的当前规范名称。
	 */
	UNPROCESSABLE_CONTENT(org.zero.common.data.constant.HttpStatus.UNPROCESSABLE_CONTENT, Series.CLIENT_ERROR, "Unprocessable Content"),
	/**
	 * 422 Unprocessable Entity，422 的旧规范名称。
	 *
	 * @deprecated 在 RFC 9110 中已更名为 {@link #UNPROCESSABLE_CONTENT}。
	 */
	@Deprecated
	UNPROCESSABLE_ENTITY(org.zero.common.data.constant.HttpStatus.UNPROCESSABLE_ENTITY, Series.CLIENT_ERROR, "Unprocessable Entity"),
	/**
	 * 423 Locked，WebDAV 资源当前处于锁定状态。
	 */
	LOCKED(org.zero.common.data.constant.HttpStatus.LOCKED, Series.CLIENT_ERROR, "Locked"),
	/**
	 * 424 Failed Dependency，WebDAV 请求因前置依赖失败而无法继续。
	 */
	FAILED_DEPENDENCY(org.zero.common.data.constant.HttpStatus.FAILED_DEPENDENCY, Series.CLIENT_ERROR, "Failed Dependency"),
	/**
	 * 425 Too Early，服务器不愿处理可能被重放的过早请求。
	 */
	TOO_EARLY(org.zero.common.data.constant.HttpStatus.TOO_EARLY, Series.CLIENT_ERROR, "Too Early"),
	/**
	 * 426 Upgrade Required，客户端需要切换到其他协议后再发起请求。
	 */
	UPGRADE_REQUIRED(org.zero.common.data.constant.HttpStatus.UPGRADE_REQUIRED, Series.CLIENT_ERROR, "Upgrade Required"),
	/**
	 * 428 Precondition Required，服务器要求请求必须附带条件头。
	 */
	PRECONDITION_REQUIRED(org.zero.common.data.constant.HttpStatus.PRECONDITION_REQUIRED, Series.CLIENT_ERROR, "Precondition Required"),
	/**
	 * 429 Too Many Requests，请求频率超过服务器允许的限制。
	 */
	TOO_MANY_REQUESTS(org.zero.common.data.constant.HttpStatus.TOO_MANY_REQUESTS, Series.CLIENT_ERROR, "Too Many Requests"),
	/**
	 * 431 Request Header Fields Too Large，请求头字段整体或单个字段过大。
	 */
	REQUEST_HEADER_FIELDS_TOO_LARGE(
		org.zero.common.data.constant.HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE,
		Series.CLIENT_ERROR,
		"Request Header Fields Too Large"
	),
	/**
	 * 451 Unavailable For Legal Reasons，资源因法律原因不可提供。
	 */
	UNAVAILABLE_FOR_LEGAL_REASONS(
		org.zero.common.data.constant.HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS,
		Series.CLIENT_ERROR,
		"Unavailable For Legal Reasons"
	),

	/* ********************************************************************************** 5xx 服务器错误 ********************************************************************************** */
	/**
	 * 500 Internal Server Error，服务器内部发生未预期错误。
	 */
	INTERNAL_SERVER_ERROR(org.zero.common.data.constant.HttpStatus.INTERNAL_SERVER_ERROR, Series.SERVER_ERROR, "Internal Server Error"),
	/**
	 * 501 Not Implemented，服务器尚不支持完成该请求所需的功能。
	 */
	NOT_IMPLEMENTED(org.zero.common.data.constant.HttpStatus.NOT_IMPLEMENTED, Series.SERVER_ERROR, "Not Implemented"),
	/**
	 * 502 Bad Gateway，作为网关或代理时收到了上游无效响应。
	 */
	BAD_GATEWAY(org.zero.common.data.constant.HttpStatus.BAD_GATEWAY, Series.SERVER_ERROR, "Bad Gateway"),
	/**
	 * 503 Service Unavailable，服务当前暂时不可用。
	 */
	SERVICE_UNAVAILABLE(org.zero.common.data.constant.HttpStatus.SERVICE_UNAVAILABLE, Series.SERVER_ERROR, "Service Unavailable"),
	/**
	 * 504 Gateway Timeout，作为网关或代理时等待上游响应超时。
	 */
	GATEWAY_TIMEOUT(org.zero.common.data.constant.HttpStatus.GATEWAY_TIMEOUT, Series.SERVER_ERROR, "Gateway Timeout"),
	/**
	 * 505 HTTP Version Not Supported，服务器不支持请求使用的 HTTP 版本。
	 */
	HTTP_VERSION_NOT_SUPPORTED(
		org.zero.common.data.constant.HttpStatus.HTTP_VERSION_NOT_SUPPORTED,
		Series.SERVER_ERROR,
		"HTTP Version Not Supported"
	),
	/**
	 * 506 Variant Also Negotiates，透明内容协商配置形成了循环。
	 */
	VARIANT_ALSO_NEGOTIATES(
		org.zero.common.data.constant.HttpStatus.VARIANT_ALSO_NEGOTIATES,
		Series.SERVER_ERROR,
		"Variant Also Negotiates"
	),
	/**
	 * 507 Insufficient Storage，服务器存储空间不足，无法完成请求。
	 */
	INSUFFICIENT_STORAGE(org.zero.common.data.constant.HttpStatus.INSUFFICIENT_STORAGE, Series.SERVER_ERROR, "Insufficient Storage"),
	/**
	 * 508 Loop Detected，服务器在处理请求时检测到无限循环。
	 */
	LOOP_DETECTED(org.zero.common.data.constant.HttpStatus.LOOP_DETECTED, Series.SERVER_ERROR, "Loop Detected"),
	/**
	 * 509 Bandwidth Limit Exceeded，表示带宽配额超限。
	 *
	 * @deprecated 非标准扩展状态码。
	 */
	@Deprecated
	BANDWIDTH_LIMIT_EXCEEDED(
		org.zero.common.data.constant.HttpStatus.BANDWIDTH_LIMIT_EXCEEDED,
		Series.SERVER_ERROR,
		"Bandwidth Limit Exceeded"
	),
	/**
	 * 510 Not Extended，表示请求所需的扩展未被满足。
	 *
	 * @deprecated IANA 注册表已将其标记为 OBSOLETED，建议仅为兼容历史实现保留。
	 */
	@Deprecated
	NOT_EXTENDED(org.zero.common.data.constant.HttpStatus.NOT_EXTENDED, Series.SERVER_ERROR, "Not Extended (OBSOLETED)"),
	/**
	 * 511 Network Authentication Required，客户端需要先完成网络接入认证。
	 */
	NETWORK_AUTHENTICATION_REQUIRED(
		org.zero.common.data.constant.HttpStatus.NETWORK_AUTHENTICATION_REQUIRED,
		Series.SERVER_ERROR,
		"Network Authentication Required"
	);

	private final int value;

	private final Series series;

	private final String reasonPhrase;

	/**
	 * 返回状态码数值。
	 *
	 * @return 状态码数值
	 */
	public int value() {
		return value;
	}

	/**
	 * 返回状态码所属大类。
	 *
	 * @return 状态码大类
	 */
	public Series series() {
		return series;
	}

	/**
	 * 返回 HTTP reason phrase。
	 *
	 * @return 英文 reason phrase
	 */
	public String reasonPhrase() {
		return reasonPhrase;
	}

	/**
	 * 判断当前状态码是否属于 1xx 信息响应。
	 *
	 * @return 是否属于 1xx
	 * @see #series()
	 */
	public boolean is1xxInformational() {
		return (series() == Series.INFORMATIONAL);
	}

	/**
	 * 判断当前状态码是否属于 2xx 成功响应。
	 *
	 * @return 是否属于 2xx
	 * @see #series()
	 */
	public boolean is2xxSuccessful() {
		return (series() == Series.SUCCESSFUL);
	}

	/**
	 * 判断当前状态码是否属于 3xx 重定向。
	 *
	 * @return 是否属于 3xx
	 * @see #series()
	 */
	public boolean is3xxRedirection() {
		return (series() == Series.REDIRECTION);
	}

	/**
	 * 判断当前状态码是否属于 4xx 客户端错误。
	 *
	 * @return 是否属于 4xx
	 * @see #series()
	 */
	public boolean is4xxClientError() {
		return (series() == Series.CLIENT_ERROR);
	}

	/**
	 * 判断当前状态码是否属于 5xx 服务器错误。
	 *
	 * @return 是否属于 5xx
	 * @see #series()
	 */
	public boolean is5xxServerError() {
		return (series() == Series.SERVER_ERROR);
	}

	/**
	 * 判断当前状态码是否属于错误响应。
	 *
	 * @return 是否属于 4xx 或 5xx
	 * @see #is4xxClientError()
	 * @see #is5xxServerError()
	 */
	public boolean isError() {
		return (is4xxClientError() || is5xxServerError());
	}

	@Override
	public String toString() {
		return this.value + StringPool.SPACE + name();
	}

	/**
	 * 根据状态码解析对应枚举，未命中时返回 {@code null}。
	 *
	 * @param statusCode 状态码
	 * @return 对应枚举，未命中返回 {@code null}
	 */
	public static HttpStatus resolve(int statusCode) {
		for (HttpStatus status : values()) {
			if (status.value == statusCode) {
				return status;
			}
		}
		return null;
	}

	/**
	 * 根据状态码解析对应枚举，未命中时抛出异常。
	 *
	 * @param statusCode 状态码
	 * @return 对应枚举
	 */
	public static HttpStatus valueOf(int statusCode) {
		HttpStatus status = resolve(statusCode);
		if (status == null) {
			throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
		}
		return status;
	}

	/**
	 * HTTP 状态码大类。
	 */
	@RequiredArgsConstructor
	public enum Series {
		/**
		 * 1xx 信息响应。
		 */
		INFORMATIONAL(1),
		/**
		 * 2xx 成功响应。
		 */
		SUCCESSFUL(2),
		/**
		 * 3xx 重定向。
		 */
		REDIRECTION(3),
		/**
		 * 4xx 客户端错误。
		 */
		CLIENT_ERROR(4),
		/**
		 * 5xx 服务器错误。
		 */
		SERVER_ERROR(5);

		private final int value;

		/**
		 * 返回大类数值。
		 *
		 * @return 大类数值
		 */
		public int value() {
			return value;
		}

		/**
		 * 根据状态码解析所属大类，未命中时抛出异常。
		 *
		 * @param statusCode 状态码
		 * @return 所属大类
		 */
		public static HttpStatus.Series valueOf(int statusCode) {
			Series series = resolve(statusCode);
			if (series == null) {
				throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
			}
			return series;
		}

		/**
		 * 根据状态码解析所属大类，未命中时返回 {@code null}。
		 *
		 * @param statusCode 状态码
		 * @return 所属大类，未命中返回 {@code null}
		 */
		public static Series resolve(int statusCode) {
			int seriesCode = statusCode / 100;
			for (Series series : values()) {
				if (series.value == seriesCode) {
					return series;
				}
			}
			return null;
		}
	}
}
