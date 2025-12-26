package org.zero.common.core.support.http.header;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CookieValue {
	protected Map<String, String> cookies;

	public static CookieValue parse(CharSequence text) {
		if (CharSequenceUtil.isEmpty(text)) {
			throw new IllegalArgumentException("text is empty");
		}
		Collection<String> parts = StringUtil.split(text.toString(), StringPool.SEMICOLON, true);
		Map<String, String> cookies = new LinkedHashMap<>();
		for (String part : parts) {
			String[] pair = StringUtil.split(part, StringPool.EQUAL, true).toArray(new String[0]);
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
}
