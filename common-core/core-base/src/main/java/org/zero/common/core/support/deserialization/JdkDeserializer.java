package org.zero.common.core.support.deserialization;

import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/15
 */
public class JdkDeserializer<T> implements Deserializer<T> {
	@SuppressWarnings("unchecked")
	@SneakyThrows
	@Override
	public T deserialize(InputStream inputStream) {
		@Cleanup ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
		return (T) objectInputStream.readObject();
	}
}
