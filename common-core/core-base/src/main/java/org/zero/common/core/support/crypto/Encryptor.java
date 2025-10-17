package org.zero.common.core.support.crypto;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.zero.common.data.model.util.Ordered;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/16
 */
public interface Encryptor extends Ordered {
	void encrypt(InputStream inputStream, OutputStream outputStream);

	@SneakyThrows
	default byte[] encrypt(byte[] bytes) {
		@Cleanup
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		@Cleanup
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		this.encrypt(byteArrayInputStream, byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}

	default ByteBuffer encrypt(ByteBuffer byteBuffer) {
		byte[] bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		byte[] encryptedBytes = this.encrypt(bytes);
		return ByteBuffer.wrap(encryptedBytes);
	}
}
