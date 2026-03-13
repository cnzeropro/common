package org.zero.common.core.support.http.header;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.data.constant.StringPool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/1/12
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class AcceptValue implements Iterable<AcceptValue.MediaType> {
	public static final HttpHeader HEADER = HttpHeader.ACCEPT;
	protected static final String Q = "q";

	protected final Collection<MediaType> mediaTypes;

	public static Builder builder() {
		return new Builder();
	}

	public static AcceptValue parse(CharSequence text) {
		if (CharSequenceUtil.isEmpty(text)) {
			throw new IllegalArgumentException("text is empty");
		}
		Collection<String> parts = StringUtil.split(text.toString(), StringPool.COMMA, true);
		List<MediaType> mediaTypes = parts.stream().map(MediaType::parse).collect(Collectors.toList());
		return new AcceptValue(mediaTypes);
	}

	@Override
	public Iterator<MediaType> iterator() {
		return mediaTypes.iterator();
	}

	public String toString() {
		return mediaTypes.stream().map(MediaType::toString).collect(Collectors.joining(StringPool.COMMA));
	}

	public static class MediaType extends org.zero.common.core.support.http.header.MediaType {
		public MediaType(String type, String subtype) {
			super(type, subtype);
		}

		public MediaType(String type, String subtype, double q) {
			super(type, subtype);
			this.setQ(q);
		}

		protected MediaType(String type, String subtype, Map<String, Serializable> parameters) {
			super(type, subtype, parameters);
		}

		public static MediaType parse(CharSequence text) {
			ParseResult parseResult = parse0(text);
			return new MediaType(parseResult.type, parseResult.subtype, parseResult.parameters);
		}

		public void setQ(double q) {
			parameters.put(Q, q);
		}

		public Double getQ() {
			return (Double) parameters.get(Q);
		}
	}

	public static class Builder {
		protected final Collection<MediaType> mediaTypes = new ArrayList<>();

		public Builder mediaType(MediaType... mediaType) {
			Collections.addAll(mediaTypes, mediaType);
			return this;
		}

		public Builder mediaType() {
			return this.mediaType(StringPool.WILDCARD, StringPool.WILDCARD);
		}

		public Builder mediaType(double q) {
			return this.mediaType(StringPool.WILDCARD, StringPool.WILDCARD, q);
		}

		public Builder mediaType(String type, double q) {
			return this.mediaType(type, StringPool.WILDCARD, q);
		}

		public Builder mediaType(String type) {
			return this.mediaType(type, StringPool.WILDCARD);
		}

		public Builder mediaType(String type, String subtype) {
			return this.mediaType(new MediaType(type, subtype));
		}

		public Builder mediaType(String type, String subtype, double q) {
			return this.mediaType(new MediaType(type, subtype, q));
		}

		public AcceptValue build() {
			return new AcceptValue(mediaTypes);
		}
	}
}
