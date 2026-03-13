package org.zero.common.core.support.http.header;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static org.zero.common.core.support.http.header.HeaderType.COMMON;
import static org.zero.common.core.support.http.header.HeaderType.REQUEST;
import static org.zero.common.core.support.http.header.HeaderType.RESPONSE;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/1/9
 */
@Getter
@RequiredArgsConstructor
public enum HttpHeader {
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept">Accept</a>
	 */
	ACCEPT("Accept", COMMON),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-CH">Accept-CH</a>
	 */
	ACCEPT_CH("Accept-CH", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Encoding">Accept-Encoding</a>
	 */
	ACCEPT_ENCODING("Accept-Encoding", COMMON),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Language">Accept-Language</a>
	 */
	ACCEPT_LANGUAGE("Accept-Language", REQUEST),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Patch">Accept-Patch</a>
	 */
	ACCEPT_PATCH("Accept-Patch", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Post">Accept-Post</a>
	 */
	ACCEPT_POST("Accept-Post", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Ranges">Accept-Ranges</a>
	 */
	ACCEPT_RANGES("Accept-Ranges", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Allow-Credentials">Access-Control-Allow-Credentials</a>
	 */
	ACCESS_CONTROL_ALLOW_CREDENTIALS("Access-Control-Allow-Credentials", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Allow-Headers">Access-Control-Allow-Headers</a>
	 */
	ACCESS_CONTROL_ALLOW_HEADERS("Access-Control-Allow-Headers", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Allow-Methods">Access-Control-Allow-Methods</a>
	 */
	ACCESS_CONTROL_ALLOW_METHODS("Access-Control-Allow-Methods", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Allow-Origin">Access-Control-Allow-Origin</a>
	 */
	ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Expose-Headers">Access-Control-Expose-Headers</a>
	 */
	ACCESS_CONTROL_EXPOSE_HEADERS("Access-Control-Expose-Headers", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Max-Age">Access-Control-Max-Age</a>
	 */
	ACCESS_CONTROL_MAX_AGE("Access-Control-Max-Age", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Request-Headers">Access-Control-Request-Headers</a>
	 */
	ACCESS_CONTROL_REQUEST_HEADERS("Access-Control-Request-Headers", REQUEST),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Request-Method">Access-Control-Request-Method</a>
	 */
	ACCESS_CONTROL_REQUEST_METHOD("Access-Control-Request-Method", REQUEST),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Activate-Storage-Access">Activate-Storage-Access</a>
	 */
	ACTIVATE_STORAGE_ACCESS("Activate-Storage-Access", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Age">Age</a>
	 */
	AGE("Age", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Allow">Allow</a>
	 */
	ALLOW("Allow", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Alt-Svc">Alt-Svc</a>
	 */
	ALT_SVC("Alt-Svc", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Alt-Used">Alt-Used</a>
	 */
	ALT_USED("Alt-Used", REQUEST),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Authorization">Authorization</a>
	 */
	AUTHORIZATION("Authorization", REQUEST),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control">Cache-Control</a>
	 */
	CACHE_CONTROL("Cache-Control", COMMON),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Clear-Site-Data">Clear-Site-Data</a>
	 */
	CLEAR_SITE_DATA("Clear-Site-Data", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Connection">Connection</a>
	 */
	CONNECTION("Connection", COMMON),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Digest">Content-Digest</a>
	 */
	CONTENT_DIGEST("Content-Digest", COMMON),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Disposition">Content-Disposition</a>
	 */
	CONTENT_DISPOSITION("Content-Disposition", COMMON),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding">Content-Encoding</a>
	 */
	CONTENT_ENCODING("Content-Encoding", COMMON),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Language">Content-Language</a>
	 */
	CONTENT_LANGUAGE("Content-Language", COMMON),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Length">Content-Length</a>
	 */
	CONTENT_LENGTH("Content-Length", COMMON),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Location">Content-Location</a>
	 */
	CONTENT_LOCATION("Content-Location", COMMON),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Range">Content-Range</a>
	 */
	CONTENT_RANGE("Content-Range", RESPONSE),
	/**
	 * CSP
	 *
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy">Content-Security-Policy</a>
	 */
	CONTENT_SECURITY_POLICY("Content-Security-Policy", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy-Report-Only">Content-Security-Policy-Report-Only</a>
	 */
	CONTENT_SECURITY_POLICY_REPORT_ONLY("Content-Security-Policy-Report-Only", RESPONSE),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Type">Content-Type</a>
	 */
	CONTENT_TYPE("Content-Type", COMMON),
	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cookie">Cookie</a>
	 */
	COOKIE("Cookie", REQUEST),
	/**
	 * COEP
	 *
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cross-Origin-Embedder-Policy">Cross-Origin-Embedder-Policy</a>
	 */
	CROSS_ORIGIN_EMBEDDER_POLICY("Cross-Origin-Embedder-Policy", RESPONSE),

	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Forwarded">Forwarded</a>
	 */
	FORWARDED("Forwarded", REQUEST),

	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Host">Host</a>
	 */
	HOST("Host", REQUEST),

	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Range">Range</a>
	 */
	RANGE("Range", REQUEST),

	/**
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-Forwarded-For">X-Forwarded-For</a>
	 */
	X_FORWARDED_FOR("X-Forwarded-For", REQUEST),

	;

	private final String name;
	private final HeaderType category;
}
