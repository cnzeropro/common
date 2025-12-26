package org.zero.common.core.support.http.header;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class HostValue {
	public static final String HEADER_NAME = "Host";

	/**
	 * 主机名
	 */
	protected String host;
	/**
	 * 端口（可选）
	 */
	protected Integer port;

	protected HostValue(String host) {
		this.host = host;
	}

	public static HostValue parse(CharSequence text) {
		if (CharSequenceUtil.isEmpty(text)) {
			throw new IllegalArgumentException("text is empty");
		}
		String[] parts = StringUtil.split(text.toString(), StringPool.COLON).toArray(new String[0]);
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
