package org.zero.common.data.enumeration;

import lombok.RequiredArgsConstructor;
import org.zero.common.data.constant.StringPool;

/**
 * Enumeration of HTTP status codes.
 * <p>
 * The HTTP status code series can be retrieved via {@link #series()}.
 * <p>
 * Copy from {@link org.springframework.http.HttpStatus}
 *
 * @author Zero (cnzeropro@163.com)
 * @see Series
 * @see <a href="https://www.iana.org/assignments/http-status-codes">HTTP Status Code Registry</a>
 * @see <a href="https://en.wikipedia.org/wiki/List_of_HTTP_status_codes">List of HTTP status codes - Wikipedia</a>
 * @since 2018/7/1
 */
@RequiredArgsConstructor
public enum HttpStatus {
    /* ********************************************************************************** 1xx Informational ********************************************************************************** */
    /**
     * {@code 100 Continue}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.2.1">HTTP/1.1: Semantics and Content, section 6.2.1</a>
     */
    CONTINUE(org.zero.common.data.constant.HttpStatus.CONTINUE, Series.INFORMATIONAL, "Continue"),
    /**
     * {@code 101 Switching Protocols}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.2.2">HTTP/1.1: Semantics and Content, section 6.2.2</a>
     */
    SWITCHING_PROTOCOLS(org.zero.common.data.constant.HttpStatus.SWITCHING_PROTOCOLS, Series.INFORMATIONAL, "Switching Protocols"),
    /**
     * {@code 102 Processing}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc2518#section-10.1">WebDAV</a>
     */
    PROCESSING(org.zero.common.data.constant.HttpStatus.PROCESSING, Series.INFORMATIONAL, "Processing"),
    /**
     * {@code 103 Checkpoint}.
     *
     * @see <a href="https://code.google.com/p/gears/wiki/ResumableHttpRequestsProposal">A proposal for supporting
     * resumable POST/PUT HTTP requests in HTTP/1.0</a>
     */
    CHECKPOINT(org.zero.common.data.constant.HttpStatus.CHECKPOINT, Series.INFORMATIONAL, "Checkpoint"),

    /* ********************************************************************************** 2xx Success ********************************************************************************** */
    /**
     * {@code 200 OK}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.3.1">HTTP/1.1: Semantics and Content, section 6.3.1</a>
     */
    OK(org.zero.common.data.constant.HttpStatus.OK, Series.SUCCESSFUL, "OK"),
    /**
     * {@code 201 Created}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.3.2">HTTP/1.1: Semantics and Content, section 6.3.2</a>
     */
    CREATED(org.zero.common.data.constant.HttpStatus.CREATED, Series.SUCCESSFUL, "Created"),
    /**
     * {@code 202 Accepted}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.3.3">HTTP/1.1: Semantics and Content, section 6.3.3</a>
     */
    ACCEPTED(org.zero.common.data.constant.HttpStatus.ACCEPTED, Series.SUCCESSFUL, "Accepted"),
    /**
     * {@code 203 Non-Authoritative Information}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.3.4">HTTP/1.1: Semantics and Content, section 6.3.4</a>
     */
    NON_AUTHORITATIVE_INFORMATION(org.zero.common.data.constant.HttpStatus.NON_AUTHORITATIVE_INFORMATION, Series.SUCCESSFUL, "Non-Authoritative Information"),
    /**
     * {@code 204 No Content}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.3.5">HTTP/1.1: Semantics and Content, section 6.3.5</a>
     */
    NO_CONTENT(org.zero.common.data.constant.HttpStatus.NO_CONTENT, Series.SUCCESSFUL, "No Content"),
    /**
     * {@code 205 Reset Content}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.3.6">HTTP/1.1: Semantics and Content, section 6.3.6</a>
     */
    RESET_CONTENT(org.zero.common.data.constant.HttpStatus.RESET_CONTENT, Series.SUCCESSFUL, "Reset Content"),
    /**
     * {@code 206 Partial Content}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7233#section-4.1">HTTP/1.1: Range Requests, section 4.1</a>
     */
    PARTIAL_CONTENT(org.zero.common.data.constant.HttpStatus.PARTIAL_CONTENT, Series.SUCCESSFUL, "Partial Content"),
    /**
     * {@code 207 Multi-Status}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc4918#section-13">WebDAV</a>
     */
    MULTI_STATUS(org.zero.common.data.constant.HttpStatus.MULTI_STATUS, Series.SUCCESSFUL, "Multi-Status"),
    /**
     * {@code 208 Already Reported}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc5842#section-7.1">WebDAV Binding Extensions</a>
     */
    ALREADY_REPORTED(org.zero.common.data.constant.HttpStatus.ALREADY_REPORTED, Series.SUCCESSFUL, "Already Reported"),
    /**
     * {@code 226 IM Used}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc3229#section-10.4.1">Delta encoding in HTTP</a>
     */
    IM_USED(org.zero.common.data.constant.HttpStatus.IM_USED, Series.SUCCESSFUL, "IM Used"),

    /* ********************************************************************************** 3xx Redirection ********************************************************************************** */
    /**
     * {@code 300 Multiple Choices}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.4.1">HTTP/1.1: Semantics and Content, section 6.4.1</a>
     */
    MULTIPLE_CHOICES(org.zero.common.data.constant.HttpStatus.MULTIPLE_CHOICES, Series.REDIRECTION, "Multiple Choices"),
    /**
     * {@code 301 Moved Permanently}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.4.2">HTTP/1.1: Semantics and Content, section 6.4.2</a>
     */
    MOVED_PERMANENTLY(org.zero.common.data.constant.HttpStatus.MOVED_PERMANENTLY, Series.REDIRECTION, "Moved Permanently"),
    /**
     * {@code 302 Found}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.4.3">HTTP/1.1: Semantics and Content, section 6.4.3</a>
     */
    FOUND(org.zero.common.data.constant.HttpStatus.FOUND, Series.REDIRECTION, "Found"),
    /**
     * {@code 302 Moved Temporarily}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc1945#section-9.3">HTTP/1.0, section 9.3</a>
     * @deprecated in favor of {@link #FOUND} which will be returned from {@code HttpStatus.valueOf(302)}
     */
    @Deprecated
    MOVED_TEMPORARILY(org.zero.common.data.constant.HttpStatus.MOVED_TEMPORARILY, Series.REDIRECTION, "Moved Temporarily"),
    /**
     * {@code 303 See Other}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.4.4">HTTP/1.1: Semantics and Content, section 6.4.4</a>
     */
    SEE_OTHER(org.zero.common.data.constant.HttpStatus.SEE_OTHER, Series.REDIRECTION, "See Other"),
    /**
     * {@code 304 Not Modified}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7232#section-4.1">HTTP/1.1: Conditional Requests, section 4.1</a>
     */
    NOT_MODIFIED(org.zero.common.data.constant.HttpStatus.NOT_MODIFIED, Series.REDIRECTION, "Not Modified"),
    /**
     * {@code 305 Use Proxy}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.4.5">HTTP/1.1: Semantics and Content, section 6.4.5</a>
     * @deprecated due to security concerns regarding in-band configuration of a proxy
     */
    @Deprecated
    USE_PROXY(org.zero.common.data.constant.HttpStatus.USE_PROXY, Series.REDIRECTION, "Use Proxy"),
    /**
     * {@code 307 Temporary Redirect}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.4.7">HTTP/1.1: Semantics and Content, section 6.4.7</a>
     */
    TEMPORARY_REDIRECT(org.zero.common.data.constant.HttpStatus.TEMPORARY_REDIRECT, Series.REDIRECTION, "Temporary Redirect"),
    /**
     * {@code 308 Permanent Redirect}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7238">RFC 7238</a>
     */
    PERMANENT_REDIRECT(org.zero.common.data.constant.HttpStatus.PERMANENT_REDIRECT, Series.REDIRECTION, "Permanent Redirect"),

    /* ********************************************************************************** 4xx Client Error ********************************************************************************** */
    /**
     * {@code 400 Bad Request}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.1">HTTP/1.1: Semantics and Content, section 6.5.1</a>
     */
    BAD_REQUEST(org.zero.common.data.constant.HttpStatus.BAD_REQUEST, Series.CLIENT_ERROR, "Bad Request"),
    /**
     * {@code 401 Unauthorized}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7235#section-3.1">HTTP/1.1: Authentication, section 3.1</a>
     */
    UNAUTHORIZED(org.zero.common.data.constant.HttpStatus.UNAUTHORIZED, Series.CLIENT_ERROR, "Unauthorized"),
    /**
     * {@code 402 Payment Required}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.2">HTTP/1.1: Semantics and Content, section 6.5.2</a>
     */
    PAYMENT_REQUIRED(org.zero.common.data.constant.HttpStatus.PAYMENT_REQUIRED, Series.CLIENT_ERROR, "Payment Required"),
    /**
     * {@code 403 Forbidden}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.3">HTTP/1.1: Semantics and Content, section 6.5.3</a>
     */
    FORBIDDEN(org.zero.common.data.constant.HttpStatus.FORBIDDEN, Series.CLIENT_ERROR, "Forbidden"),
    /**
     * {@code 404 Not Found}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.4">HTTP/1.1: Semantics and Content, section 6.5.4</a>
     */
    NOT_FOUND(org.zero.common.data.constant.HttpStatus.NOT_FOUND, Series.CLIENT_ERROR, "Not Found"),
    /**
     * {@code 405 Method Not Allowed}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.5">HTTP/1.1: Semantics and Content, section 6.5.5</a>
     */
    METHOD_NOT_ALLOWED(org.zero.common.data.constant.HttpStatus.METHOD_NOT_ALLOWED, Series.CLIENT_ERROR, "Method Not Allowed"),
    /**
     * {@code 406 Not Acceptable}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.6">HTTP/1.1: Semantics and Content, section 6.5.6</a>
     */
    NOT_ACCEPTABLE(org.zero.common.data.constant.HttpStatus.NOT_ACCEPTABLE, Series.CLIENT_ERROR, "Not Acceptable"),
    /**
     * {@code 407 Proxy Authentication Required}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7235#section-3.2">HTTP/1.1: Authentication, section 3.2</a>
     */
    PROXY_AUTHENTICATION_REQUIRED(org.zero.common.data.constant.HttpStatus.PROXY_AUTHENTICATION_REQUIRED, Series.CLIENT_ERROR, "Proxy Authentication Required"),
    /**
     * {@code 408 Request Timeout}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.7">HTTP/1.1: Semantics and Content, section 6.5.7</a>
     */
    REQUEST_TIMEOUT(org.zero.common.data.constant.HttpStatus.REQUEST_TIMEOUT, Series.CLIENT_ERROR, "Request Timeout"),
    /**
     * {@code 409 Conflict}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.8">HTTP/1.1: Semantics and Content, section 6.5.8</a>
     */
    CONFLICT(org.zero.common.data.constant.HttpStatus.CONFLICT, Series.CLIENT_ERROR, "Conflict"),
    /**
     * {@code 410 Gone}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.9">
     * HTTP/1.1: Semantics and Content, section 6.5.9</a>
     */
    GONE(org.zero.common.data.constant.HttpStatus.GONE, Series.CLIENT_ERROR, "Gone"),
    /**
     * {@code 411 Length Required}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.10">
     * HTTP/1.1: Semantics and Content, section 6.5.10</a>
     */
    LENGTH_REQUIRED(org.zero.common.data.constant.HttpStatus.LENGTH_REQUIRED, Series.CLIENT_ERROR, "Length Required"),
    /**
     * {@code 412 Precondition failed}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7232#section-4.2">
     * HTTP/1.1: Conditional Requests, section 4.2</a>
     */
    PRECONDITION_FAILED(org.zero.common.data.constant.HttpStatus.PRECONDITION_FAILED, Series.CLIENT_ERROR, "Precondition Failed"),
    /**
     * {@code 413 Payload Too Large}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.11">
     * HTTP/1.1: Semantics and Content, section 6.5.11</a>
     */
    PAYLOAD_TOO_LARGE(org.zero.common.data.constant.HttpStatus.PAYLOAD_TOO_LARGE, Series.CLIENT_ERROR, "Payload Too Large"),
    /**
     * {@code 413 Request Entity Too Large}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc2616#section-10.4.14">HTTP/1.1, section 10.4.14</a>
     * @deprecated in favor of {@link #PAYLOAD_TOO_LARGE} which will be
     * returned from {@code HttpStatus.valueOf(413)}
     */
    @Deprecated
    REQUEST_ENTITY_TOO_LARGE(org.zero.common.data.constant.HttpStatus.REQUEST_ENTITY_TOO_LARGE, Series.CLIENT_ERROR, "Request Entity Too Large"),
    /**
     * {@code 414 URI Too Long}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.12">
     * HTTP/1.1: Semantics and Content, section 6.5.12</a>
     */
    URI_TOO_LONG(org.zero.common.data.constant.HttpStatus.URI_TOO_LONG, Series.CLIENT_ERROR, "URI Too Long"),
    /**
     * {@code 414 Request-URI Too Long}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc2616#section-10.4.15">HTTP/1.1, section 10.4.15</a>
     * @deprecated in favor of {@link #URI_TOO_LONG} which will be returned from {@code HttpStatus.valueOf(414)}
     */
    @Deprecated
    REQUEST_URI_TOO_LONG(org.zero.common.data.constant.HttpStatus.REQUEST_URI_TOO_LONG, Series.CLIENT_ERROR, "Request-URI Too Long"),
    /**
     * {@code 415 Unsupported Media Type}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.13">
     * HTTP/1.1: Semantics and Content, section 6.5.13</a>
     */
    UNSUPPORTED_MEDIA_TYPE(org.zero.common.data.constant.HttpStatus.UNSUPPORTED_MEDIA_TYPE, Series.CLIENT_ERROR, "Unsupported Media Type"),
    /**
     * {@code 416 Requested Range Not Satisfiable}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7233#section-4.4">HTTP/1.1: Range Requests, section 4.4</a>
     */
    REQUESTED_RANGE_NOT_SATISFIABLE(org.zero.common.data.constant.HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, Series.CLIENT_ERROR, "Requested range not satisfiable"),
    /**
     * {@code 417 Expectation Failed}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.14">
     * HTTP/1.1: Semantics and Content, section 6.5.14</a>
     */
    EXPECTATION_FAILED(org.zero.common.data.constant.HttpStatus.EXPECTATION_FAILED, Series.CLIENT_ERROR, "Expectation Failed"),
    /**
     * {@code 418 I'm a teapot}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc2324#section-2.3.2">HTCPCP/1.0</a>
     */
    I_AM_A_TEAPOT(org.zero.common.data.constant.HttpStatus.I_AM_A_TEAPOT, Series.CLIENT_ERROR, "I'm a teapot"),
    /**
     * @deprecated See
     * <a href="https://tools.ietf.org/rfcdiff?difftype=--hwdiff&amp;url2=draft-ietf-webdav-protocol-06.txt">
     * WebDAV Draft Changes</a>
     */
    @Deprecated
    INSUFFICIENT_SPACE_ON_RESOURCE(org.zero.common.data.constant.HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE, Series.CLIENT_ERROR, "Insufficient Space On Resource"),
    /**
     * @deprecated See
     * <a href="https://tools.ietf.org/rfcdiff?difftype=--hwdiff&amp;url2=draft-ietf-webdav-protocol-06.txt">
     * WebDAV Draft Changes</a>
     */
    @Deprecated
    METHOD_FAILURE(org.zero.common.data.constant.HttpStatus.METHOD_FAILURE, Series.CLIENT_ERROR, "Method Failure"),
    /**
     * @deprecated See <a href="https://tools.ietf.org/rfcdiff?difftype=--hwdiff&amp;url2=draft-ietf-webdav-protocol-06.txt">
     * WebDAV Draft Changes</a>
     */
    @Deprecated
    DESTINATION_LOCKED(org.zero.common.data.constant.HttpStatus.DESTINATION_LOCKED, Series.CLIENT_ERROR, "Destination Locked"),
    /**
     * {@code 422 Unprocessable Entity}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc4918#section-11.2">WebDAV</a>
     */
    UNPROCESSABLE_ENTITY(org.zero.common.data.constant.HttpStatus.UNPROCESSABLE_ENTITY, Series.CLIENT_ERROR, "Unprocessable Entity"),
    /**
     * {@code 423 Locked}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc4918#section-11.3">WebDAV</a>
     */
    LOCKED(org.zero.common.data.constant.HttpStatus.LOCKED, Series.CLIENT_ERROR, "Locked"),
    /**
     * {@code 424 Failed Dependency}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc4918#section-11.4">WebDAV</a>
     */
    FAILED_DEPENDENCY(org.zero.common.data.constant.HttpStatus.FAILED_DEPENDENCY, Series.CLIENT_ERROR, "Failed Dependency"),
    /**
     * {@code 425 Too Early}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc8470">RFC 8470</a>
     */
    TOO_EARLY(org.zero.common.data.constant.HttpStatus.TOO_EARLY, Series.CLIENT_ERROR, "Too Early"),
    /**
     * {@code 426 Upgrade Required}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc2817#section-6">Upgrading to TLS Within HTTP/1.1</a>
     */
    UPGRADE_REQUIRED(org.zero.common.data.constant.HttpStatus.UPGRADE_REQUIRED, Series.CLIENT_ERROR, "Upgrade Required"),
    /**
     * {@code 428 Precondition Required}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc6585#section-3">Additional HTTP Status Codes</a>
     */
    PRECONDITION_REQUIRED(org.zero.common.data.constant.HttpStatus.PRECONDITION_REQUIRED, Series.CLIENT_ERROR, "Precondition Required"),
    /**
     * {@code 429 Too Many Requests}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc6585#section-4">Additional HTTP Status Codes</a>
     */
    TOO_MANY_REQUESTS(org.zero.common.data.constant.HttpStatus.TOO_MANY_REQUESTS, Series.CLIENT_ERROR, "Too Many Requests"),
    /**
     * {@code 431 Request Header Fields Too Large}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc6585#section-5">Additional HTTP Status Codes</a>
     */
    REQUEST_HEADER_FIELDS_TOO_LARGE(org.zero.common.data.constant.HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE, Series.CLIENT_ERROR, "Request Header Fields Too Large"),
    /**
     * {@code 451 Unavailable For Legal Reasons}.
     *
     * @see <a href="https://tools.ietf.org/html/draft-ietf-httpbis-legally-restricted-status-04">
     * An HTTP Status Code to Report Legal Obstacles</a>
     */
    UNAVAILABLE_FOR_LEGAL_REASONS(org.zero.common.data.constant.HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS, Series.CLIENT_ERROR, "Unavailable For Legal Reasons"),

    /* ********************************************************************************** 5xx Server Error ********************************************************************************** */
    /**
     * {@code 500 Internal Server Error}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.1">HTTP/1.1: Semantics and Content, section 6.6.1</a>
     */
    INTERNAL_SERVER_ERROR(org.zero.common.data.constant.HttpStatus.INTERNAL_SERVER_ERROR, Series.SERVER_ERROR, "Internal Server Error"),
    /**
     * {@code 501 Not Implemented}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.2">HTTP/1.1: Semantics and Content, section 6.6.2</a>
     */
    NOT_IMPLEMENTED(org.zero.common.data.constant.HttpStatus.NOT_IMPLEMENTED, Series.SERVER_ERROR, "Not Implemented"),
    /**
     * {@code 502 Bad Gateway}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.3">HTTP/1.1: Semantics and Content, section 6.6.3</a>
     */
    BAD_GATEWAY(org.zero.common.data.constant.HttpStatus.BAD_GATEWAY, Series.SERVER_ERROR, "Bad Gateway"),
    /**
     * {@code 503 Service Unavailable}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.4">HTTP/1.1: Semantics and Content, section 6.6.4</a>
     */
    SERVICE_UNAVAILABLE(org.zero.common.data.constant.HttpStatus.SERVICE_UNAVAILABLE, Series.SERVER_ERROR, "Service Unavailable"),
    /**
     * {@code 504 Gateway Timeout}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.5">HTTP/1.1: Semantics and Content, section 6.6.5</a>
     */
    GATEWAY_TIMEOUT(org.zero.common.data.constant.HttpStatus.GATEWAY_TIMEOUT, Series.SERVER_ERROR, "Gateway Timeout"),
    /**
     * {@code 505 HTTP Version Not Supported}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.6">HTTP/1.1: Semantics and Content, section 6.6.6</a>
     */
    HTTP_VERSION_NOT_SUPPORTED(org.zero.common.data.constant.HttpStatus.HTTP_VERSION_NOT_SUPPORTED, Series.SERVER_ERROR, "HTTP Version not supported"),
    /**
     * {@code 506 Variant Also Negotiates}
     *
     * @see <a href="https://tools.ietf.org/html/rfc2295#section-8.1">Transparent Content Negotiation</a>
     */
    VARIANT_ALSO_NEGOTIATES(org.zero.common.data.constant.HttpStatus.VARIANT_ALSO_NEGOTIATES, Series.SERVER_ERROR, "Variant Also Negotiates"),
    /**
     * {@code 507 Insufficient Storage}
     *
     * @see <a href="https://tools.ietf.org/html/rfc4918#section-11.5">WebDAV</a>
     */
    INSUFFICIENT_STORAGE(org.zero.common.data.constant.HttpStatus.INSUFFICIENT_STORAGE, Series.SERVER_ERROR, "Insufficient Storage"),
    /**
     * {@code 508 Loop Detected}
     *
     * @see <a href="https://tools.ietf.org/html/rfc5842#section-7.2">WebDAV Binding Extensions</a>
     */
    LOOP_DETECTED(org.zero.common.data.constant.HttpStatus.LOOP_DETECTED, Series.SERVER_ERROR, "Loop Detected"),
    /**
     * {@code 509 Bandwidth Limit Exceeded}
     */
    BANDWIDTH_LIMIT_EXCEEDED(org.zero.common.data.constant.HttpStatus.BANDWIDTH_LIMIT_EXCEEDED, Series.SERVER_ERROR, "Bandwidth Limit Exceeded"),
    /**
     * {@code 510 Not Extended}
     *
     * @see <a href="https://tools.ietf.org/html/rfc2774#section-7">HTTP Extension Framework</a>
     */
    NOT_EXTENDED(org.zero.common.data.constant.HttpStatus.NOT_EXTENDED, Series.SERVER_ERROR, "Not Extended"),
    /**
     * {@code 511 Network Authentication Required}.
     *
     * @see <a href="https://tools.ietf.org/html/rfc6585#section-6">Additional HTTP Status Codes</a>
     */
    NETWORK_AUTHENTICATION_REQUIRED(org.zero.common.data.constant.HttpStatus.NETWORK_AUTHENTICATION_REQUIRED, Series.SERVER_ERROR, "Network Authentication Required");

    private final int value;

    private final Series series;

    private final String reasonPhrase;

    public int value() {
        return value;
    }

    public Series series() {
        return series;
    }

    public String reasonPhrase() {
        return reasonPhrase;
    }

    /**
     * Whether this status code is in the HTTP series
     * {@link Series#INFORMATIONAL}.
     * <p>This is a shortcut for checking the value of {@link #series()}.
     *
     * @see #series()
     */
    public boolean is1xxInformational() {
        return (series() == Series.INFORMATIONAL);
    }

    /**
     * Whether this status code is in the HTTP series
     * {@link Series#SUCCESSFUL}.
     * <p>This is a shortcut for checking the value of {@link #series()}.
     *
     * @see #series()
     */
    public boolean is2xxSuccessful() {
        return (series() == Series.SUCCESSFUL);
    }

    /**
     * Whether this status code is in the HTTP series
     * {@link Series#REDIRECTION}.
     * <p>This is a shortcut for checking the value of {@link #series()}.
     *
     * @see #series()
     */
    public boolean is3xxRedirection() {
        return (series() == Series.REDIRECTION);
    }

    /**
     * Whether this status code is in the HTTP series
     * {@link Series#CLIENT_ERROR}.
     * <p>This is a shortcut for checking the value of {@link #series()}.
     *
     * @see #series()
     */
    public boolean is4xxClientError() {
        return (series() == Series.CLIENT_ERROR);
    }

    /**
     * Whether this status code is in the HTTP series
     * {@link Series#SERVER_ERROR}.
     * <p>This is a shortcut for checking the value of {@link #series()}.
     *
     * @see #series()
     */
    public boolean is5xxServerError() {
        return (series() == Series.SERVER_ERROR);
    }

    /**
     * Whether this status code is in the HTTP series
     * {@link Series#CLIENT_ERROR} or
     * {@link Series#SERVER_ERROR}.
     * <p>This is a shortcut for checking the value of {@link #series()}.
     *
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

    public static HttpStatus resolve(int statusCode) {
        for (HttpStatus status : values()) {
            if (status.value == statusCode) {
                return status;
            }
        }
        return null;
    }

    public static HttpStatus valueOf(int statusCode) {
        HttpStatus status = resolve(statusCode);
        if (status == null) {
            throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
        }
        return status;
    }

    @RequiredArgsConstructor
    public enum Series {
        INFORMATIONAL(1),
        SUCCESSFUL(2),
        REDIRECTION(3),
        CLIENT_ERROR(4),
        SERVER_ERROR(5);

        private final int value;

        public int value() {
            return value;
        }

        public static HttpStatus.Series valueOf(int statusCode) {
            Series series = resolve(statusCode);
            if (series == null) {
                throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
            }
            return series;
        }

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
