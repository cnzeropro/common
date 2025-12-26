package org.zero.common.core.support.http.header;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.data.constant.StringPool;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/X-Forwarded-For">X-Forwarded-For</a>
 * @since 2025/12/23
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class XForwardedForValue implements Iterable<String> {
	public static final String HEADER_NAME = "X-Forwarded-For";

	protected Collection<String> ips;

	public static XForwardedForValue parse(CharSequence text) {
		if (CharSequenceUtil.isEmpty(text)) {
			throw new IllegalArgumentException("text is empty");
		}
		Collection<String> ips = StringUtil.split(text.toString(), StringPool.COMMA);
		return new XForwardedForValue(ips);
	}

	public String getClientIp() {
		return this.iterator().next();
	}

	public Collection<String> getProxyIps() {
		return ips.stream().skip(1L).collect(Collectors.toList());
	}

	@Override
	public Iterator<String> iterator() {
		return ips.iterator();
	}

	@Override
	public String toString() {
		return String.join(StringPool.COMMA, ips);
	}
}
