package org.zero.common.core.support.api.crypto.converter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.zero.common.core.extension.java.lang.Base16;
import org.zero.common.core.support.codec.Base64Codec;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/31
 */
public interface StringMode extends InputConverter<CharSequence>, OutputConverter<CharSequence> {
	@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
	abstract class HexMode implements StringMode {
		protected final Base16 base16;

		@Override
		public CharSequence fromBytes(byte[] bytes) {
			char[] chars = base16.encode(bytes);
			return String.valueOf(chars);
		}

		@Override
		public byte[] toBytes(CharSequence charSequence) {
			return base16.decode(charSequence);
		}
	}

	class HexLower extends HexMode {
		public static final HexLower INSTANCE = new HexLower();

		protected HexLower() {
			super(Base16.LOWER);
		}
	}

	class HexUpper extends HexMode {
		public static final HexUpper INSTANCE = new HexUpper();

		protected HexUpper() {
			super(Base16.UPPER);
		}
	}

	@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
	class Base64Mode implements StringMode {
		protected final Base64Codec base64Codec;
		protected final CharsetMode charsetMode;

		@Override
		public CharSequence fromBytes(byte[] bytes) {
			byte[] encodedBytes = base64Codec.encode(bytes);
			return charsetMode.fromBytes(encodedBytes);
		}

		@Override
		public byte[] toBytes(CharSequence charSequence) {
			byte[] bytes = charsetMode.toBytes(charSequence);
			return base64Codec.decode(bytes);
		}
	}

	class UrlSafeBase64AndUtf8 extends Base64Mode {
		public static final UrlSafeBase64AndUtf8 INSTANCE = new UrlSafeBase64AndUtf8();

		protected UrlSafeBase64AndUtf8() {
			super(Base64Codec.URL_SAFE, Utf8.INSTANCE);
		}
	}

	@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
	abstract class CharsetMode implements StringMode {
		protected final Charset charset;

		@Override
		public CharSequence fromBytes(byte[] bytes) {
			return new String(bytes, charset);
		}

		@Override
		public byte[] toBytes(CharSequence charSequence) {
			return charSequence.toString().getBytes(charset);
		}
	}

	class Utf8 extends CharsetMode {
		public static final Utf8 INSTANCE = new Utf8();

		protected Utf8() {
			super(StandardCharsets.UTF_8);
		}
	}

	@Override
	CharSequence fromBytes(byte[] bytes);

	@Override
	byte[] toBytes(CharSequence charSequence);

	@Override
	default Class<?>[] supportTypes() {
		return new Class[]{CharSequence.class};
	}

	@Override
	default boolean supports(Object object) {
		return object instanceof CharSequence;
	}
}
