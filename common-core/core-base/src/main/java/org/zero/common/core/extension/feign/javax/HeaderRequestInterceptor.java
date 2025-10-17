package org.zero.common.core.extension.feign.javax;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.spring.web.context.request.javax.RequestUtil;

import javax.servlet.http.HttpServletRequest;
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
