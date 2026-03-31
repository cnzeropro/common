package org.zero.common.data.constant;

/**
 * HTTP 状态码常量。
 * <p>
 * 规范名称以当前 IANA HTTP Status Code Registry 与现行 RFC 为准。
 * 为兼容历史实现，本接口保留了历史别名、已废弃状态码以及非标准扩展状态码；
 * 其中不再建议直接使用的定义统一标注 {@link Deprecated}。
 *
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://www.iana.org/assignments/http-status-codes/http-status-codes.xhtml">HTTP Status Code Registry</a>
 * @since 2025/5/4
 */
public interface HttpStatus {
	/* ****************************************** 1xx 信息响应 ****************************************** */
	/**
	 * 100 Continue，请求头已被接收，客户端可以继续发送请求体。
	 */
	int CONTINUE = 100;
	/**
	 * 101 Switching Protocols，服务器将根据 Upgrade 头切换协议。
	 */
	int SWITCHING_PROTOCOLS = 101;
	/**
	 * 102 Processing，表示服务器正在处理请求但尚未完成最终响应。
	 *
	 * @deprecated IANA 仍保留该注册项，但 RFC 4918 已移除其定义，建议仅为兼容历史实现保留。
	 */
	@Deprecated
	int PROCESSING = 102;
	/**
	 * 103 Early Hints，在最终响应前提前返回提示头，便于客户端预加载资源。
	 */
	int EARLY_HINTS = 103;
	/**
	 * 103 Checkpoint，非标准扩展中对 103 的历史命名。
	 *
	 * @deprecated 非标准历史别名，请使用 {@link #EARLY_HINTS}。
	 */
	@Deprecated
	int CHECKPOINT = 103;

	/* ****************************************** 2xx 成功响应 ****************************************** */
	/**
	 * 200 OK，请求已成功处理。
	 */
	int OK = 200;
	/**
	 * 201 Created，请求成功并创建了新的资源。
	 */
	int CREATED = 201;
	/**
	 * 202 Accepted，请求已被接受，但尚未完成处理。
	 */
	int ACCEPTED = 202;
	/**
	 * 203 Non-Authoritative Information，返回的是副本或转换后的非权威信息。
	 */
	int NON_AUTHORITATIVE_INFORMATION = 203;
	/**
	 * 204 No Content，请求成功，但响应中没有消息体。
	 */
	int NO_CONTENT = 204;
	/**
	 * 205 Reset Content，请求成功，客户端应重置当前视图或表单状态。
	 */
	int RESET_CONTENT = 205;
	/**
	 * 206 Partial Content，返回资源的部分内容，常用于范围请求。
	 */
	int PARTIAL_CONTENT = 206;
	/**
	 * 207 Multi-Status，WebDAV 多状态响应，可在响应体中携带多个子结果。
	 */
	int MULTI_STATUS = 207;
	/**
	 * 208 Already Reported，WebDAV 中表示同一绑定资源已在前文报告过。
	 */
	int ALREADY_REPORTED = 208;
	/**
	 * 226 IM Used，表示服务器已对资源应用实例操作后返回结果。
	 */
	int IM_USED = 226;

	/* ****************************************** 3xx 重定向 ****************************************** */
	/**
	 * 300 Multiple Choices，目标资源存在多个可供选择的表示。
	 */
	int MULTIPLE_CHOICES = 300;
	/**
	 * 301 Moved Permanently，目标资源已被永久移动到新的 URI。
	 */
	int MOVED_PERMANENTLY = 301;
	/**
	 * 302 Found，目标资源临时位于其他 URI。
	 */
	int FOUND = 302;
	/**
	 * 302 Moved Temporarily，302 的历史命名。
	 *
	 * @deprecated 历史别名，请使用 {@link #FOUND}。
	 */
	@Deprecated
	int MOVED_TEMPORARILY = 302;
	/**
	 * 303 See Other，建议客户端改用 GET 到其他 URI 获取结果。
	 */
	int SEE_OTHER = 303;
	/**
	 * 304 Not Modified，资源未发生变化，客户端可继续使用缓存副本。
	 */
	int NOT_MODIFIED = 304;
	/**
	 * 305 Use Proxy，要求通过代理访问目标资源。
	 *
	 * @deprecated 已被 RFC 9110 废弃，不建议继续使用。
	 */
	@Deprecated
	int USE_PROXY = 305;
	/**
	 * 306 (Unused)，保留且未使用。
	 *
	 * @deprecated 在 RFC 9110 中仍为保留未使用状态码。
	 */
	@Deprecated
	int UNUSED_306 = 306;
	/**
	 * 307 Temporary Redirect，临时重定向且应保持原请求方法与请求体语义。
	 */
	int TEMPORARY_REDIRECT = 307;
	/**
	 * 308 Permanent Redirect，永久重定向且应保持原请求方法与请求体语义。
	 */
	int PERMANENT_REDIRECT = 308;

	/* ****************************************** 4xx 客户端错误 ****************************************** */
	/**
	 * 400 Bad Request，请求语法或参数不符合服务器要求。
	 */
	int BAD_REQUEST = 400;
	/**
	 * 401 Unauthorized，当前请求需要通过身份认证。
	 */
	int UNAUTHORIZED = 401;
	/**
	 * 402 Payment Required，保留用于未来可能的支付相关场景。
	 */
	int PAYMENT_REQUIRED = 402;
	/**
	 * 403 Forbidden，服务器理解请求但拒绝执行。
	 */
	int FORBIDDEN = 403;
	/**
	 * 404 Not Found，目标资源不存在。
	 */
	int NOT_FOUND = 404;
	/**
	 * 405 Method Not Allowed，请求方法不被目标资源允许。
	 */
	int METHOD_NOT_ALLOWED = 405;
	/**
	 * 406 Not Acceptable，服务器无法提供满足内容协商条件的表示。
	 */
	int NOT_ACCEPTABLE = 406;
	/**
	 * 407 Proxy Authentication Required，请求方需要先通过代理认证。
	 */
	int PROXY_AUTHENTICATION_REQUIRED = 407;
	/**
	 * 408 Request Timeout，服务器等待请求超时。
	 */
	int REQUEST_TIMEOUT = 408;
	/**
	 * 409 Conflict，请求与目标资源当前状态发生冲突。
	 */
	int CONFLICT = 409;
	/**
	 * 410 Gone，目标资源已永久移除且预期不会恢复。
	 */
	int GONE = 410;
	/**
	 * 411 Length Required，请求缺少必需的内容长度信息。
	 */
	int LENGTH_REQUIRED = 411;
	/**
	 * 412 Precondition Failed，请求中的前置条件未满足。
	 */
	int PRECONDITION_FAILED = 412;
	/**
	 * 413 Content Too Large，请求内容过大。
	 * 这是 RFC 9110 中的当前规范名称。
	 */
	int CONTENT_TOO_LARGE = 413;
	/**
	 * 413 Payload Too Large，413 的旧规范名称。
	 *
	 * @deprecated 在 RFC 9110 中已更名为 {@link #CONTENT_TOO_LARGE}。
	 */
	@Deprecated
	int PAYLOAD_TOO_LARGE = 413;
	/**
	 * 413 Request Entity Too Large，413 的历史命名。
	 *
	 * @deprecated 历史别名，请使用 {@link #CONTENT_TOO_LARGE}。
	 */
	@Deprecated
	int REQUEST_ENTITY_TOO_LARGE = 413;
	/**
	 * 414 URI Too Long，请求目标 URI 过长。
	 */
	int URI_TOO_LONG = 414;
	/**
	 * 414 Request-URI Too Long，414 的历史命名。
	 *
	 * @deprecated 历史别名，请使用 {@link #URI_TOO_LONG}。
	 */
	@Deprecated
	int REQUEST_URI_TOO_LONG = 414;
	/**
	 * 415 Unsupported Media Type，请求消息体的媒体类型不受支持。
	 */
	int UNSUPPORTED_MEDIA_TYPE = 415;
	/**
	 * 416 Range Not Satisfiable，请求的范围无法满足。
	 * 这是 RFC 9110 中的当前规范名称。
	 */
	int RANGE_NOT_SATISFIABLE = 416;
	/**
	 * 416 Requested Range Not Satisfiable，416 的旧规范名称。
	 *
	 * @deprecated 在 RFC 9110 中已更名为 {@link #RANGE_NOT_SATISFIABLE}。
	 */
	@Deprecated
	int REQUESTED_RANGE_NOT_SATISFIABLE = 416;
	/**
	 * 417 Expectation Failed，服务器无法满足 Expect 请求头中的期望。
	 */
	int EXPECTATION_FAILED = 417;
	/**
	 * 418 (Unused)，当前规范将该状态码保留且未使用。
	 *
	 * @deprecated 在 RFC 9110 中被保留为未使用状态码。
	 */
	@Deprecated
	int UNUSED_418 = 418;
	/**
	 * 418 I'm a teapot，历史上的玩笑性质扩展状态码。
	 *
	 * @deprecated 非标准历史扩展；现行 RFC 已将 418 保留为未使用状态码。
	 */
	@Deprecated
	int I_AM_A_TEAPOT = 418;
	/**
	 * 419 Insufficient Space on Resource，表示目标资源可用空间不足。
	 *
	 * @deprecated 非标准历史 WebDAV 扩展状态码。
	 */
	@Deprecated
	int INSUFFICIENT_SPACE_ON_RESOURCE = 419;
	/**
	 * 420 Method Failure，表示方法执行失败。
	 *
	 * @deprecated 非标准历史 WebDAV 扩展状态码。
	 */
	@Deprecated
	int METHOD_FAILURE = 420;
	/**
	 * 421 Misdirected Request，请求被发送到了无法生成目标响应的服务器。
	 */
	int MISDIRECTED_REQUEST = 421;
	/**
	 * 421 Destination Locked，历史上与 421 共享数值的非标准命名。
	 *
	 * @deprecated 非标准历史别名，请使用 {@link #MISDIRECTED_REQUEST}。
	 */
	@Deprecated
	int DESTINATION_LOCKED = 421;
	/**
	 * 422 Unprocessable Content，请求内容语法正确，但语义上无法处理。
	 * 这是 RFC 9110 中的当前规范名称。
	 */
	int UNPROCESSABLE_CONTENT = 422;
	/**
	 * 422 Unprocessable Entity，422 的旧规范名称。
	 *
	 * @deprecated 在 RFC 9110 中已更名为 {@link #UNPROCESSABLE_CONTENT}。
	 */
	@Deprecated
	int UNPROCESSABLE_ENTITY = 422;
	/**
	 * 423 Locked，WebDAV 资源当前处于锁定状态。
	 */
	int LOCKED = 423;
	/**
	 * 424 Failed Dependency，WebDAV 请求因前置依赖失败而无法继续。
	 */
	int FAILED_DEPENDENCY = 424;
	/**
	 * 425 Too Early，服务器不愿处理可能被重放的过早请求。
	 */
	int TOO_EARLY = 425;
	/**
	 * 426 Upgrade Required，客户端需要切换到其他协议后再发起请求。
	 */
	int UPGRADE_REQUIRED = 426;
	/**
	 * 428 Precondition Required，服务器要求请求必须附带条件头。
	 */
	int PRECONDITION_REQUIRED = 428;
	/**
	 * 429 Too Many Requests，请求频率超过服务器允许的限制。
	 */
	int TOO_MANY_REQUESTS = 429;
	/**
	 * 431 Request Header Fields Too Large，请求头字段整体或单个字段过大。
	 */
	int REQUEST_HEADER_FIELDS_TOO_LARGE = 431;
	/**
	 * 451 Unavailable For Legal Reasons，资源因法律原因不可提供。
	 */
	int UNAVAILABLE_FOR_LEGAL_REASONS = 451;

	/* ***************************************** 5xx 服务器错误 ****************************************** */
	/**
	 * 500 Internal Server Error，服务器内部发生未预期错误。
	 */
	int INTERNAL_SERVER_ERROR = 500;
	/**
	 * 501 Not Implemented，服务器尚不支持完成该请求所需的功能。
	 */
	int NOT_IMPLEMENTED = 501;
	/**
	 * 502 Bad Gateway，作为网关或代理时收到了上游无效响应。
	 */
	int BAD_GATEWAY = 502;
	/**
	 * 503 Service Unavailable，服务当前暂时不可用。
	 */
	int SERVICE_UNAVAILABLE = 503;
	/**
	 * 504 Gateway Timeout，作为网关或代理时等待上游响应超时。
	 */
	int GATEWAY_TIMEOUT = 504;
	/**
	 * 505 HTTP Version Not Supported，服务器不支持请求使用的 HTTP 版本。
	 */
	int HTTP_VERSION_NOT_SUPPORTED = 505;
	/**
	 * 506 Variant Also Negotiates，透明内容协商配置形成了循环。
	 */
	int VARIANT_ALSO_NEGOTIATES = 506;
	/**
	 * 507 Insufficient Storage，服务器存储空间不足，无法完成请求。
	 */
	int INSUFFICIENT_STORAGE = 507;
	/**
	 * 508 Loop Detected，服务器在处理请求时检测到无限循环。
	 */
	int LOOP_DETECTED = 508;
	/**
	 * 509 Bandwidth Limit Exceeded，表示带宽配额超限。
	 *
	 * @deprecated 非标准扩展状态码。
	 */
	@Deprecated
	int BANDWIDTH_LIMIT_EXCEEDED = 509;
	/**
	 * 510 Not Extended，表示请求所需的扩展未被满足。
	 *
	 * @deprecated IANA 注册表已将其标记为 OBSOLETED，建议仅为兼容历史实现保留。
	 */
	@Deprecated
	int NOT_EXTENDED = 510;
	/**
	 * 511 Network Authentication Required，客户端需要先完成网络接入认证。
	 */
	int NETWORK_AUTHENTICATION_REQUIRED = 511;
}
