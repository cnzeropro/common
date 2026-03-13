package org.zero.common.core.support.http.header;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.data.constant.StringPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/1/12
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class AcceptEncodingValue implements Iterable<AcceptEncodingValue.Encoding> {
	public static final HttpHeader HEADER = HttpHeader.ACCEPT_ENCODING;

	protected final Collection<Encoding> encodings;

	public static Builder builder() {
		return new Builder();
	}

	public static AcceptEncodingValue parse(CharSequence text) {
		if (CharSequenceUtil.isEmpty(text)) {
			throw new IllegalArgumentException("text is empty");
		}
		Collection<String> parts = StringUtil.split(text.toString(), StringPool.COMMA, true);
		List<Encoding> encodings = parts.stream().map(Encoding::parse).collect(Collectors.toList());
		return new AcceptEncodingValue(encodings);
	}


	@Override
	public Iterator<Encoding> iterator() {
		return encodings.iterator();
	}

	@Override
	public String toString() {
		return encodings.stream().map(Encoding::toString).collect(Collectors.joining(StringPool.COMMA + StringPool.SPACE));
	}

	@Getter
	@EqualsAndHashCode
	@RequiredArgsConstructor
	public static class Encoding {
		protected final String compressionAlgorithm;
		protected final Double q;

		public Encoding(String compressionAlgorithm) {
			this(compressionAlgorithm, null);
		}

		public static Encoding parse(CharSequence text) {
			if (CharSequenceUtil.isEmpty(text)) {
				throw new IllegalArgumentException("text is empty");
			}
			String[] parts = StringUtil.splitToArray(text.toString(), StringPool.SEMICOLON, true);
			if (parts.length > 1) {
				String[] pair = StringUtil.splitToArray(parts[1], StringPool.EQUAL, true);
				if (pair.length != 2) {
					throw new IllegalArgumentException(text + " is invalid");
				}
				if (!pair[0].equals("q")) {
					throw new IllegalArgumentException(text + " is invalid");
				}
				return new Encoding(parts[0], Double.parseDouble(pair[1]));
			}
			return new Encoding(parts[0]);
		}

		@Override
		public String toString() {
			if (Objects.isNull(q)) {
				return compressionAlgorithm;
			}
			return compressionAlgorithm + StringPool.SEMICOLON + "q=" + q;
		}
	}

	public static class Builder {
		protected final Collection<Encoding> encodings = new ArrayList<>();

		public Builder encoding(String compressionAlgorithm) {
			encodings.add(new Encoding(compressionAlgorithm));
			return this;
		}

		public Builder encoding(String compressionAlgorithm, Double q) {
			encodings.add(new Encoding(compressionAlgorithm, q));
			return this;
		}

		public AcceptEncodingValue build() {
			return new AcceptEncodingValue(encodings);
		}
	}
}
