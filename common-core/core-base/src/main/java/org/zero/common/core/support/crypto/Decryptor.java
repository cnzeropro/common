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
public interface Decryptor extends Ordered {

	void decrypt(InputStream inputStream, OutputStream outputStream);

	@SneakyThrows
	default byte[] decrypt(byte[] bytes) {
		@Cleanup
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		@Cleanup
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		this.decrypt(byteArrayInputStream, byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}

	default ByteBuffer decrypt(ByteBuffer byteBuffer) {
		byte[] bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		byte[] decryptedBytes = this.decrypt(bytes);
		return ByteBuffer.wrap(decryptedBytes);
	}
}
