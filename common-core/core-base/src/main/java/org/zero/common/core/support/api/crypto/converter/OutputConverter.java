package org.zero.common.core.support.api.crypto.converter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/12
 */
@FunctionalInterface
public interface OutputConverter<T>  {
	T fromBytes(byte[] bytes);
}
