package org.zero.common.data.model.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 键值对，实现了 {@link Map.Entry} 接口，可用于表示简单的二元组。
 *
 * @param <K> 键的类型
 * @param <V> 值的类型
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair<K, V> implements Map.Entry<K, V> {

    /** 键 */
    private K key;

    /** 值 */
    private V value;

    /**
     * 设置新的值。
     *
     * @param value 新值
     * @return 新设置的值
     */
	@Override
	public V setValue(V value) {
		this.value = value;
		return value;
	}
}