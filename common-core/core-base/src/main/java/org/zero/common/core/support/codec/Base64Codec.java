package org.zero.common.core.support.codec;

import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.io.IoUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/15
 */
@RequiredArgsConstructor
public class Base64Codec implements Codec {
	public static final Base64Codec BASIC = new Base64Codec(Type.BASIC);
	public static final Base64Codec URL_SAFE = new Base64Codec(Type.URL);
	public static final Base64Codec MIME = new Base64Codec(Type.MIME);


	protected final Base64.Encoder encoder;
	protected final Base64.Decoder decoder;

	public Base64Codec() {
		this(Type.BASIC);
	}

	public Base64Codec(Type type) {
		this(type, false);
	}

	public Base64Codec(boolean withoutPadding) {
		this(Type.BASIC, withoutPadding);
	}

	public Base64Codec(Type type, boolean withoutPadding) {
		Base64.Encoder encoder;
		Base64.Decoder decoder;
		switch (type) {
			case URL:
				encoder = Base64.getUrlEncoder();
				decoder = Base64.getUrlDecoder();
				break;
			case MIME:
				encoder = Base64.getMimeEncoder();
				decoder = Base64.getMimeDecoder();
				break;
			default:
				encoder = Base64.getEncoder();
				decoder = Base64.getDecoder();
				break;
		}
		this.encoder = withoutPadding ? encoder.withoutPadding() : encoder;
		this.decoder = decoder;
	}

	@Override
	public void encode(InputStream inputStream, OutputStream outputStream) {
		OutputStream encoderOutputStream = encoder.wrap(outputStream);
		IoUtil.copy(inputStream, encoderOutputStream);
	}

	@Override
	public void decode(InputStream inputStream, OutputStream outputStream) {
		InputStream decoderInputStream = decoder.wrap(inputStream);
		IoUtil.copy(decoderInputStream, outputStream);
	}

	public enum Type {
		/**
		 * 基本。使用 RFC 4648 标准，不添加换行符
		 */
		BASIC,
		/**
		 * URL 安全。使用 RFC 4648 标准，将 + 和 / 分别替换为 - 和 _
		 */
		URL,
		/**
		 * MIME 类型。使用 RFC 2045 标准，每 76 个字符换行，并使用 CRLF（"\r\n"）作为行分隔符
		 */
		MIME
	}
}
