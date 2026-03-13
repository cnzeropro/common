package org.zero.common.core.support.http.header;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.data.constant.StringPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/X-Forwarded-For">X-Forwarded-For</a>
 * @since 2025/12/23
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class XForwardedForValue implements Iterable<String> {
	public static final HttpHeader HEADER = HttpHeader.X_FORWARDED_FOR;

	protected final Collection<String> ips;

	public static XForwardedForValue parse(CharSequence text) {
		if (CharSequenceUtil.isEmpty(text)) {
			throw new IllegalArgumentException("text is empty");
		}
		Collection<String> ips = StringUtil.split(text.toString(), StringPool.COMMA, true);
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
		return String.join(StringPool.COMMA + StringPool.SPACE, ips);
	}

	public static class Builder {
		protected final List<String> ips = new ArrayList<>();

		public Builder clientIp(String clientIp) {
			if (ips.isEmpty()) {
				ips.add(clientIp);
			} else {
				ips.set(0, clientIp);
			}
			return this;
		}

		public Builder proxyIps(String... proxyIps) {
			if (ips.isEmpty()) {
				ips.add(StringPool.EMPTY);
			}
			Collections.addAll(ips, proxyIps);
			return this;
		}

		public Builder ips(String... ips) {
			Collections.addAll(this.ips, ips);
			return this;
		}

		public XForwardedForValue build() {
			return new XForwardedForValue(ips);
		}
	}
}
