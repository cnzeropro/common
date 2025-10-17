package org.zero.common.core.support.bean.map;

import java.util.Map;
import java.util.Objects;

/**
 * Map 值处理器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/24
 */
public abstract class MapValueHandler<V> {

	protected final boolean ignoreNull;
	protected final boolean append;

	protected MapValueHandler(boolean ignoreNull, boolean append) {
		this.ignoreNull = ignoreNull;
		this.append = append;
	}

	/**
	 * 处理
	 *
	 * @param objectTree 待处理对象树
	 * @param map        Map
	 */
	protected void handle(ObjectTree objectTree, Map<String, V> map) {
		V convertedObject = this.convert(objectTree.object, objectTree.beanProperty);
		if (Objects.isNull(convertedObject) && this.ignoreNull) {
			return;
		}
		this.addToMap(objectTree, map, convertedObject);
	}

	/**
	 * 对象转换
	 *
	 * @param object       待转换对象
	 * @param beanProperty Bean 属性信息
	 * @return Map 值
	 */
	protected abstract V convert(Object object, BeanProperty beanProperty);

	/**
	 * 添加到 Map 中
	 *
	 * @param objectTree 对象树
	 * @param map        Map
	 * @param value      值
	 */
	protected abstract void addToMap(ObjectTree objectTree, Map<String, V> map, V value);
}
