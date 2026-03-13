package org.zero.common.core.support.http.header;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.data.constant.StringPool;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/1/12
 */
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
public class ContentDispositionValue {
	public static final HttpHeader HEADER = HttpHeader.CONTENT_DISPOSITION;

	@Getter
	protected final String type;
	@Getter
	protected String name;
	@Getter
	protected String filename;
	protected Charset charset;

	public ContentDispositionValue(String type, String name) {
		this(type, name, null, null);
	}

	public ContentDispositionValue(String type, String name, String filename) {
		this(type, name, filename, null);
	}

	public ContentDispositionValue(String type, String filename, Charset charset) {
		this(type, null, filename, charset);
	}

	public static final String ATTACHMENT = "attachment";
	public static final String INLINE = "inline";
	public static final String FORM_DATA = "form-data";

	public static Builder attachment() {
		return builder(ATTACHMENT);
	}

	public static Builder inline() {
		return builder(INLINE);
	}

	public static Builder formData() {
		return builder(FORM_DATA);
	}

	public static Builder builder(String type) {
		return new Builder(type);
	}

	protected static final String NAME = "name";
	protected static final String FILENAME = "filename";
	protected static final String FILENAME_STAR = FILENAME + StringPool.ASTERISK;

	@SneakyThrows
	public static ContentDispositionValue parse(CharSequence text) {
		if (CharSequenceUtil.isEmpty(text)) {
			throw new IllegalArgumentException("text is empty");
		}
		String[] parts = StringUtil.splitToArray(text.toString(), StringPool.SEMICOLON, true);
		ContentDispositionValue contentDispositionValue = new ContentDispositionValue(parts[0]);
		for (int i = 1; i < parts.length; i++) {
			String[] pair = StringUtil.splitToArray(parts[i], StringPool.EQUAL, true);
			if (pair.length != 2) {
				throw new IllegalArgumentException(parts[i] + " is invalid");
			}
			if (NAME.equalsIgnoreCase(pair[0])) {
				contentDispositionValue.name = StringUtil.stripWrappingQuotes(pair[1]);
			} else if (FILENAME.equalsIgnoreCase(pair[0])) {
				contentDispositionValue.filename = StringUtil.stripWrappingQuotes(pair[1]);
			} else if (FILENAME_STAR.equalsIgnoreCase(pair[0])) {
				String[] filenameParts = StringUtil.splitToArray(pair[1], StringPool.SINGLE_QUOTE + StringPool.SINGLE_QUOTE, true);
				contentDispositionValue.charset = Charset.forName(filenameParts[0]);
				contentDispositionValue.filename = URLDecoder.decode(filenameParts[1], filenameParts[0]);
			} else {
				throw new IllegalArgumentException(pair[0] + " is invalid");
			}
		}
		return contentDispositionValue;
	}

	public boolean isAttachment() {
		return ATTACHMENT.equalsIgnoreCase(type);
	}


	public boolean isInline() {
		return INLINE.equalsIgnoreCase(type);
	}

	public boolean isFormData() {
		return FORM_DATA.equalsIgnoreCase(type);
	}

	@SneakyThrows
	public String getEncodeFilename() {
		if (Objects.isNull(charset) || StandardCharsets.US_ASCII.equals(charset)) {
			return filename;
		}
		return URLEncoder.encode(filename, charset.name());
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(type);
		if (Objects.nonNull(name)) {
			stringBuilder.append(StringPool.SEMICOLON + StringPool.SPACE)
				.append(NAME).append(StringPool.EQUAL)
				.append(StringPool.DOUBLE_QUOTE).append(name).append(StringPool.DOUBLE_QUOTE);
		}
		if (Objects.nonNull(filename)) {
			stringBuilder.append(StringPool.SEMICOLON + StringPool.SPACE);
			if (Objects.isNull(charset) || StandardCharsets.US_ASCII.equals(charset)) {
				stringBuilder.append(FILENAME).append(StringPool.EQUAL)
					.append(StringPool.DOUBLE_QUOTE).append(filename).append(StringPool.DOUBLE_QUOTE);
			} else {
				stringBuilder.append(FILENAME_STAR).append(StringPool.EQUAL)
					.append(charset)
					.append(StringPool.SINGLE_QUOTE + StringPool.SINGLE_QUOTE)
					.append(this.getEncodeFilename());
			}
		}
		return stringBuilder.toString();
	}

	public static class Builder {
		protected String type;
		protected String name;
		protected String filename;
		protected Charset charset;

		public Builder(String type) {
			this.type = type;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder filename(String filename) {
			return this.filename(filename, (Charset) null);
		}

		public Builder filename(String filename, String charset) {
			return this.filename(filename, Objects.isNull(charset) ? null : Charset.forName(charset));
		}

		public Builder filename(String filename, Charset charset) {
			this.filename = filename;
			this.charset = charset;
			return this;
		}

		public ContentDispositionValue build() {
			return new ContentDispositionValue(type, name, filename, charset);
		}
	}
}
