package org.zero.common.core.support.api.crypto;

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
public interface StringMode {
	@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
	abstract class HexMode implements StringMode {
		public final Base16 base16;

		@Override
		public String toString(byte[] bytes) {
			char[] chars = base16.encode(bytes);
			return String.valueOf(chars);
		}

		@Override
		public byte[] toBytes(String string) {
			return base16.decode(string);
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
		public String toString(byte[] bytes) {
			byte[] encodedBytes = base64Codec.encode(bytes);
			return charsetMode.toString(encodedBytes);
		}

		@Override
		public byte[] toBytes(String string) {
			byte[] bytes = charsetMode.toBytes(string);
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
		public final Charset charset;

		@Override
		public String toString(byte[] bytes) {
			return new String(bytes, charset);
		}

		@Override
		public byte[] toBytes(String string) {
			return string.getBytes(charset);
		}
	}

	class Utf8 extends CharsetMode {
		public static final Utf8 INSTANCE = new Utf8();

		protected Utf8() {
			super(StandardCharsets.UTF_8);
		}
	}

	String toString(byte[] bytes);

	byte[] toBytes(String string);
}
