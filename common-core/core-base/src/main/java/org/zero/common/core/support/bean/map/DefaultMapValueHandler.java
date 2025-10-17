package org.zero.common.core.support.bean.map;

import org.zero.common.core.util.java.lang.ArrayUtil;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.core.util.java.lang.ObjectUtil;
import org.zero.common.core.util.java.util.ListUtil;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/24
 */
public class DefaultMapValueHandler extends MapValueHandler<Object> {
	public DefaultMapValueHandler() {
		this(true, false);
	}

	public DefaultMapValueHandler(boolean ignoreNull, boolean append) {
		super(ignoreNull, append);
	}

	@Override
	public Object convert(Object object, BeanProperty beanProperty) {
		if (ObjectUtil.isMultivariable(object) && ignoreNull) {
			return Arrays.stream(ArrayUtil.of(object)).filter(Objects::nonNull).toArray();
		}
		return object;
	}

	@Override
	public void addToMap(ObjectTree objectTree, Map<String, Object> map, Object value) {
		String key = objectTree.key;
		if (!append) {
			map.put(key, value);
			return;
		}
		map.compute(key, (k, v) -> {
			Type type = objectTree.type;
			Class<?> clazz = ClassUtil.getRawClass(type);
			boolean multivariable = ClassUtil.isMultivariable(clazz);
			Object[] objects;
			if (Objects.isNull(value)) {
				if (multivariable) {
					objects = new Object[0];
				} else {
					objects = new Object[]{null};
				}
			} else {
				objects = ArrayUtil.of(value);
			}
			if (Objects.isNull(v)) {
				if (multivariable) {
					return ListUtil.of(objects);
				}
				return value;
			}
			Object[] array = ArrayUtil.of(v);
			List<Object> list = ListUtil.of(array);
			Collections.addAll(list, objects);
			return list;
		});
	}
}
