package org.zero.common.core.support.http.header;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.data.constant.StringPool;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Cookie">Cookie</a>
 * @since 2025/12/24
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class CookieValue {
	public static final HttpHeader HEADER = HttpHeader.COOKIE;

	protected final Map<String, String> cookies;

	public static Builder builder() {
		return new Builder();
	}

	public static CookieValue parse(CharSequence text) {
		if (CharSequenceUtil.isEmpty(text)) {
			throw new IllegalArgumentException("text is empty");
		}
		Collection<String> parts = StringUtil.split(text.toString(), StringPool.SEMICOLON, true);
		Map<String, String> cookies = new LinkedHashMap<>();
		for (String part : parts) {
			String[] pair = StringUtil.splitToArray(part, StringPool.EQUAL, true);
			if (pair.length != 2) {
				throw new IllegalArgumentException(part + " is invalid");
			}
			String name = pair[0];
			String value = pair[1];
			cookies.put(name, value);
		}
		return new CookieValue(cookies);
	}

	@Override
	public String toString() {
		StringJoiner stringJoiner = new StringJoiner(StringPool.SEMICOLON + StringPool.SPACE);
		cookies.forEach((name, value) -> stringJoiner.add(name + StringPool.EQUAL + value));
		return stringJoiner.toString();
	}

	public static class Builder {
		protected final Map<String, String> cookies = new LinkedHashMap<>();

		public Builder cookies(Map<String, String> cookies) {
			this.cookies.putAll(cookies);
			return this;
		}

		public Builder cookie(String name, String value) {
			cookies.put(name, value);
			return this;
		}

		public CookieValue build() {
			return new CookieValue(cookies);
		}
	}
}
