package org.zero.common.core.support.codec;

import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * 解码器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/15
 */
public interface Decoder {
	void decode(InputStream inputStream, OutputStream outputStream);

	@SneakyThrows
	default byte[] decode(byte[] bytes) {
		@Cleanup
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		@Cleanup
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		this.decode(byteArrayInputStream, byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}

	default ByteBuffer decode(ByteBuffer byteBuffer) {
		byte[] bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		byte[] decodedBytes = this.decode(bytes);
		return ByteBuffer.wrap(decodedBytes);
	}
}
