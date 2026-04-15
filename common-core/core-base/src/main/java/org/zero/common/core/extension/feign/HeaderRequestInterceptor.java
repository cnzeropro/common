package org.zero.common.core.extension.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.zero.common.core.util.spring.web.context.request.javax.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/28
 */
public class HeaderRequestInterceptor implements RequestInterceptor {
	protected final Mode mode;
	protected final String[] headerNames;

	public HeaderRequestInterceptor(String... headerNames) {
		this(Mode.INCLUDE, headerNames);
	}

	public HeaderRequestInterceptor(Mode mode, String... headerNames) {
		this.mode = Objects.requireNonNull(mode, "Mode must not be null");
		this.headerNames = normalizeHeaderNames(headerNames);
	}

	protected static String[] normalizeHeaderNames(String[] headerNames) {
		LinkedHashMap<String, String> result = new LinkedHashMap<>();
		if (Objects.isNull(headerNames)) {
			return new String[0];
		}
		for (String headerName : headerNames) {
			String normalizedHeaderName = normalizeHeaderName(headerName);
			if (Objects.isNull(normalizedHeaderName) || result.containsKey(normalizedHeaderName)) {
				continue;
			}
			result.put(normalizedHeaderName, headerName.trim());
		}
		return result.values().toArray(new String[0]);
	}

	protected static String normalizeHeaderName(String headerName) {
		if (Objects.isNull(headerName)) {
			return null;
		}
		String trimmedHeaderName = headerName.trim();
		if (trimmedHeaderName.isEmpty()) {
			return null;
		}
		return trimmedHeaderName.toLowerCase(Locale.ROOT);
	}

	protected boolean containsHeaderName(String headerName) {
		if (Objects.isNull(headerName)) {
			return false;
		}
		for (String configuredHeaderName : headerNames) {
			if (configuredHeaderName.equalsIgnoreCase(headerName)) {
				return true;
			}
		}
		return false;
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
			if (containsHeaderName(headerName)) {
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
