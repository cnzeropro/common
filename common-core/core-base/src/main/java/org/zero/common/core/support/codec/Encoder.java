package org.zero.common.core.support.codec;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.zero.common.data.model.util.Ordered;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * 编码器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/15
 */
public interface Encoder extends Ordered {
	void encode(InputStream inputStream, OutputStream outputStream);

	@SneakyThrows
	default byte[] encode(byte[] bytes) {
		@Cleanup ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		@Cleanup ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		this.encode(byteArrayInputStream, byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}

	default ByteBuffer encode(ByteBuffer byteBuffer) {
		byte[] bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		byte[] encodedBytes = this.encode(bytes);
		return ByteBuffer.wrap(encodedBytes);
	}
}
