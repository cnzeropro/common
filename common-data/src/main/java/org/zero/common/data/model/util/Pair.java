package org.zero.common.data.model.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair<K, V> implements Map.Entry<K, V> {
    private K key;
    private V value;

	@Override
	public V setValue(V value) {
		this.value = value;
		return value;
	}
}