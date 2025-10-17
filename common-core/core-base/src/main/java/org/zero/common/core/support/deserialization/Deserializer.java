package org.zero.common.core.support.deserialization;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.zero.common.data.model.util.Ordered;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/15
 */
public interface Deserializer<T> extends Ordered {
	T deserialize(InputStream inputStream);

	@SneakyThrows
	default T deserialize(byte[] bytes) {
		@Cleanup ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		return this.deserialize(byteArrayInputStream);
	}

	default T deserialize(ByteBuffer byteBuffer) {
		byte[] bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		return this.deserialize(bytes);
	}
}
