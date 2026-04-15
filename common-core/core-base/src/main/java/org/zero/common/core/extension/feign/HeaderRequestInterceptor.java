package org.zero.common.core.extension.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.zero.common.core.util.spring.web.context.request.javax.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/28
 */
public class HeaderRequestInterceptor implements RequestInterceptor {
	protected final Mode mode;
	protected final String[] headerNames;
	protected final Map<String, String> normalizedHeaderNames;

	public HeaderRequestInterceptor(String... headerNames) {
		this(Mode.INCLUDE, headerNames);
	}

	public HeaderRequestInterceptor(Mode mode, String... headerNames) {
		this.mode = Objects.requireNonNull(mode, "Mode must not be null");
		LinkedHashMap<String, String> normalizedHeaderNames = normalizeHeaderNames(headerNames);
		this.headerNames = normalizedHeaderNames.values().toArray(new String[0]);
		this.normalizedHeaderNames = Collections.unmodifiableMap(normalizedHeaderNames);
	}

	protected static LinkedHashMap<String, String> normalizeHeaderNames(String[] headerNames) {
		LinkedHashMap<String, String> result = new LinkedHashMap<>();
		if (Objects.isNull(headerNames)) {
			return result;
		}
		for (String headerName : headerNames) {
			String normalizedHeaderName = normalizeHeaderName(headerName);
			if (Objects.isNull(normalizedHeaderName) || result.containsKey(normalizedHeaderName)) {
				continue;
			}
			result.put(normalizedHeaderName, headerName.trim());
		}
		return result;
	}

	protected static String normalizeHeaderName(String headerName) {
		if (Objects.isNull(headerName)) {
			return null;
		}
		String trimmedHeaderName = headerName.trim();
		if (trimmedHeaderName.isEmpty()) {
			return null;
		}
		return trimmedHeaderName.toLowerCase();
	}

	@Override
	public void apply(RequestTemplate requestTemplate) {
		HttpServletRequest request = RequestUtil.getHttpServletRequest();
		if (Objects.isNull(request)) {
			return;
		}
		if (Mode.EXCLUDE == mode) {
			applyExclude(requestTemplate, request);
			return;
		}
		applyInclude(requestTemplate, request);
	}

	protected void applyInclude(RequestTemplate requestTemplate, HttpServletRequest request) {
		for (String headerName : headerNames) {
			String headerValue = request.getHeader(headerName);
			if (Objects.nonNull(headerValue)) {
				requestTemplate.header(headerName, headerValue);
			}
		}
	}

	protected void applyExclude(RequestTemplate requestTemplate, HttpServletRequest request) {
		Enumeration<String> requestHeaderNames = request.getHeaderNames();
		if (Objects.isNull(requestHeaderNames)) {
			return;
		}
		while (requestHeaderNames.hasMoreElements()) {
			String headerName = requestHeaderNames.nextElement();
			if (normalizedHeaderNames.containsKey(normalizeHeaderName(headerName))) {
				continue;
			}
			String headerValue = request.getHeader(headerName);
			if (Objects.nonNull(headerValue)) {
				requestTemplate.header(headerName, headerValue);
			}
		}
	}

	public enum Mode {
		INCLUDE,
		EXCLUDE
	}
}
