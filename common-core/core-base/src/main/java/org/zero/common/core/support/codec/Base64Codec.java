package org.zero.common.core.support.codec;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.zero.common.core.util.java.io.IoUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/15
 */
@NoArgsConstructor
@AllArgsConstructor
public class Base64Codec implements Codec {
	public static final Base64Codec INSTANCE = new Base64Codec();

	protected Base64.Encoder encoder = Base64.getEncoder();
	protected Base64.Decoder decoder = Base64.getDecoder();

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
}
