package org.zero.common.core.support.http.header;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.lang.CharSequenceUtil;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/1/12
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class ContentTypeValue {
	public static final HttpHeader HEADER = HttpHeader.CONTENT_TYPE;

	protected final MediaType mediaType;

	public static ContentTypeValue parse(CharSequence text) {
		if (CharSequenceUtil.isEmpty(text)) {
			throw new IllegalArgumentException("text is empty");
		}
		MediaType mediaType = MediaType.parse(text);
		return new ContentTypeValue(mediaType);
	}

	@Override
	public String toString() {
		return mediaType.toString();
	}
}
