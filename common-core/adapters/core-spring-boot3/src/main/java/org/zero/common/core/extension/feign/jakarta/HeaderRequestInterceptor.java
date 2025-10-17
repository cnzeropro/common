package org.zero.common.core.extension.feign.jakarta;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.spring.web.context.request.javax.RequestUtil;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/28
 */
@RequiredArgsConstructor
public class HeaderRequestInterceptor implements RequestInterceptor {
	protected final String[] headerNames;

	@Override
	public void apply(RequestTemplate requestTemplate) {
		HttpServletRequest request = RequestUtil.getHttpServletRequest();
		if (Objects.nonNull(request)) {
			for (String headerName : headerNames) {
				String headerValue = request.getHeader(headerName);
				if (Objects.nonNull(headerValue)) {
					requestTemplate.header(headerName, headerValue);
				}
			}
		}
	}
}
