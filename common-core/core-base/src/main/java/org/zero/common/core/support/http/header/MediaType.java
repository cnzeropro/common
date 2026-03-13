package org.zero.common.core.support.http.header;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.data.constant.StringPool;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/1/12
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class MediaType {
	public static final MediaType ALL = new MediaType(StringPool.WILDCARD, StringPool.WILDCARD);

	protected final String type;
	protected final String subtype;
	protected final Map<String, Serializable> parameters;


	public MediaType(final String type, final String subtype) {
		this.type = type;
		this.subtype = subtype;
		this.parameters = new LinkedHashMap<>();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static MediaType parse(CharSequence text) {
		ParseResult parseResult = parse0(text);
		return new MediaType(parseResult.type, parseResult.subtype, parseResult.parameters);
	}

	protected static ParseResult parse0(CharSequence text) {
		if (CharSequenceUtil.isEmpty(text)) {
			throw new IllegalArgumentException("text is empty");
		}
		String[] parts = StringUtil.splitToArray(text.toString(), StringPool.SEMICOLON, true);
		if (parts.length < 2) {
			throw new IllegalArgumentException(text + " is invalid");
		}
		String[] types = StringUtil.splitToArray(text.toString(), StringPool.SLASH, true);
		Map<String, Serializable> parameters = new LinkedHashMap<>();
		for (int i = 1; i < parts.length; i++) {
			String[] pair = StringUtil.splitToArray(text.toString(), StringPool.SEMICOLON, true);
			if (pair.length != 2) {
				throw new IllegalArgumentException(text + " is invalid");
			}
			parameters.put(pair[0], pair[1]);
		}
		return new ParseResult(types[0], types[1], parameters);
	}

	@Override
	public String toString() {
		StringJoiner stringJoiner = new StringJoiner(StringPool.SEMICOLON + StringPool.SPACE,
			type + StringPool.SLASH + subtype,
			StringPool.EMPTY);
		parameters.forEach((name, value) -> stringJoiner.add(name + StringPool.EQUAL + value));
		return stringJoiner.toString();
	}

	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	protected static class ParseResult {
		protected String type;
		protected String subtype;
		protected Map<String, Serializable> parameters;
	}

	public static class Builder {
		protected String type;
		protected String subtype;
		protected Map<String, Serializable> parameters = new LinkedHashMap<>();

		public Builder type(String type) {
			this.type = type;
			return this;
		}

		public Builder subtype(String subtype) {
			this.subtype = subtype;
			return this;
		}

		public Builder parameters(Map<String, Serializable> parameters) {
			this.parameters.putAll(parameters);
			return this;
		}

		public Builder parameter(String name, Serializable value) {
			this.parameters.put(name, value);
			return this;
		}

		public MediaType build() {
			return new MediaType(type, subtype, parameters);
		}
	}
}
