package org.zero.common.core.support.serialization;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.zero.common.data.model.util.Ordered;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/15
 */
@FunctionalInterface
public interface Serializer<T> extends Ordered {
	default boolean isSupported(T obj) {
		return true;
	}

	void serialize(T obj, OutputStream outputStream);

	@SneakyThrows
	default byte[] serializeToBytes(T obj) {
		@Cleanup ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		serialize(obj, byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}

	default ByteBuffer serializeToByteBuffer(T obj) {
		byte[] bytes = this.serializeToBytes(obj);
		return ByteBuffer.wrap(bytes);
	}
}
