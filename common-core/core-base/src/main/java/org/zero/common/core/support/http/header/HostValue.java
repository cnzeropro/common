package org.zero.common.core.support.http.header;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.data.constant.StringPool;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Host">Host</a>
 * @since 2025/12/23
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
public class HostValue {
	public static final HttpHeader HEADER = HttpHeader.HOST;

	/**
	 * 主机名
	 */
	protected final String host;
	/**
	 * 端口（可选）
	 */
	protected Integer port;

	public static HostValue parse(CharSequence text) {
		if (CharSequenceUtil.isEmpty(text)) {
			throw new IllegalArgumentException("text is empty");
		}
		String[] parts = StringUtil.splitToArray(text.toString(), StringPool.COLON, true);
		if (parts.length == 1) {
			return new HostValue(parts[0]);
		}
		if (parts.length == 2) {
			int port = Integer.parseInt(parts[1]);
			return new HostValue(parts[0], port);
		}
		throw new IllegalArgumentException(text + " is invalid");
	}

	@Override
	public String toString() {
		return host + (Objects.nonNull(port) ? StringPool.COLON + port : StringPool.EMPTY);
	}
}
