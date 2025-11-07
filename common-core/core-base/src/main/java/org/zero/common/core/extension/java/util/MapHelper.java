package org.zero.common.core.extension.java.util;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.zero.common.data.constant.ConstantPool;

import java.util.Map;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/3
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public class MapHelper {
	protected final Map<?, ?> map;

	public Object get(Object key) {
		return map.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> getClass(Object key) {
		return this.getOpt(key, Class.class).orElse(null);
	}

	public String getString(Object key) {
		return this.get(key, String.class);
	}

	public byte getByte(Object key) {
		return this.getOpt(key, Byte.class).orElse(ConstantPool.BYTE_ZERO);
	}

	public short getShort(Object key) {
		return this.getOpt(key, Short.class).orElse(ConstantPool.SHORT_ZERO);
	}

	public int getInt(Object key) {
		return this.getOpt(key, Integer.class).orElse(ConstantPool.INT_ZERO);
	}

	public long getLong(Object key) {
		return this.getOpt(key, Long.class).orElse(ConstantPool.LONG_ZERO);
	}

	public float getFloat(Object key) {
		return this.getOpt(key, Float.class).orElse(ConstantPool.FLOAT_ZERO);
	}

	public double getDouble(Object key) {
		return this.getOpt(key, Double.class).orElse(ConstantPool.DOUBLE_ZERO);
	}

	public char getChar(Object key) {
		return this.getOpt(key, Character.class).orElse(ConstantPool.CHAR_ZERO);
	}

	public boolean getBoolean(Object key) {
		return this.getOpt(key, Boolean.class).orElse(ConstantPool.BOOLEAN_FALSE);
	}

	public <T> T get(Object key, Class<T> clazz) {
		return this.getOpt(key, clazz).orElse(null);
	}

	public Optional<Object> getOpt(Object key) {
		Object value = this.get(key);
		return Optional.ofNullable(value);
	}

	public <T> Optional<T> getOpt(Object key, Class<T> clazz) {
		return this.getOpt(key).filter(clazz::isInstance).map(clazz::cast);
	}
}
