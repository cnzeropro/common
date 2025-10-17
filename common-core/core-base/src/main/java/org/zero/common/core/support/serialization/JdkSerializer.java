package org.zero.common.core.support.serialization;

import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/15
 */
public class JdkSerializer<T> implements Serializer<T> {
	@Override
	public boolean isSupported(T obj) {
		return obj instanceof Serializable;
	}

	@SneakyThrows
	@Override
	public void serialize(T obj, OutputStream outputStream) {
		@Cleanup ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
		objectOutputStream.writeObject(obj);
	}
}
