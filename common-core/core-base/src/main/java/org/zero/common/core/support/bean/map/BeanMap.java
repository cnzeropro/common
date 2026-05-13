package org.zero.common.core.support.bean.map;

import feign.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.zero.common.core.extension.java.lang.reflect.TypeReference;
import org.zero.common.core.extension.java.util.Option;
import org.zero.common.core.util.java.lang.ArrayUtil;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.core.util.java.lang.ObjectUtil;
import org.zero.common.core.util.java.lang.reflect.FieldUtil;
import org.zero.common.core.util.java.lang.reflect.MethodUtil;
import org.zero.common.core.util.java.util.CollectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/3
 */
public class BeanMap<V> {
	protected final MapValueHandler<V> mapValueHandler;
	protected Collection<ObjectConfig> objectConfigs = new ArrayList<>();
	protected DirectEvaluator directEvaluator = DefaultDirectEvaluator.INSTANCE;
	protected BeanEvaluator beanEvaluator = DefaultBeanEvaluator.INSTANCE;
	protected long maxDeep = Long.MAX_VALUE;

	protected final Map<String, Long> sizeMap = new LinkedHashMap<>();

	protected BeanMap(MapValueHandler<V> mapValueHandler) {
		this.mapValueHandler = mapValueHandler;
	}

	protected BeanMap(MapValueHandler<V> mapValueHandler, ObjectConfig... objectConfigs) {
		this.mapValueHandler = mapValueHandler;
		Collections.addAll(this.objectConfigs, objectConfigs);
	}

	protected BeanMap(MapValueHandler<V> mapValueHandler, Collection<ObjectConfig> objectConfigs) {
		this.mapValueHandler = mapValueHandler;
		this.objectConfigs = objectConfigs;
	}

	public static BeanMap<Object> of() {
		return of(new DefaultMapValueHandler());
	}

	public static <V> BeanMap<V> of(MapValueHandler<V> mapValueHandler) {
		return new BeanMap<>(mapValueHandler);
	}

	public static <V> BeanMap<V> of(MapValueHandler<V> mapValueHandler, ObjectConfig... objectConfigs) {
		return new BeanMap<>(mapValueHandler, objectConfigs);
	}

	public static <V> BeanMap<V> of(MapValueHandler<V> mapValueHandler, Collection<ObjectConfig> objectConfigs) {
		return new BeanMap<>(mapValueHandler, objectConfigs);
	}

	public BeanMap<V> objectConfig(ObjectConfig objectConfig) {
		this.objectConfigs.add(objectConfig);
		return this;
	}

	public BeanMap<V> objectConfigs(Collection<ObjectConfig> objectConfigs) {
		this.objectConfigs.addAll(objectConfigs);
		return this;
	}

	public BeanMap<V> objectConfigs(ObjectConfig... objectConfigs) {
		Collections.addAll(this.objectConfigs, objectConfigs);
		return this;
	}

	public BeanMap<V> maxDeep(long maxDeep) {
		this.maxDeep = maxDeep;
		return this;
	}

	public BeanMap<V> beanEvaluator(BeanEvaluator beanEvaluator) {
		this.beanEvaluator = beanEvaluator;
		return this;
	}

	public BeanMap<V> directEvaluator(DirectEvaluator directEvaluator) {
		this.directEvaluator = directEvaluator;
		return this;
	}

	public Map<String, V> toMap() {
		return this.toMap(new LinkedHashMap<>());
	}

	public Map<String, V> toMap(Supplier<Map<String, V>> mapSupplier) {
		return this.toMap(mapSupplier.get());
	}

	public Map<String, V> toMap(Map<String, V> map) {
		for (ObjectConfig objectConfig : objectConfigs) {
			ObjectTree root = ObjectTree.of(objectConfig);
			this.tree(root);
			this.inMap(root, map);
		}
		return map;
	}

	protected void inMap(ObjectTree objectTree, Map<String, V> map) {
		if (maxDeep < 0) {
			return;
		}
		ObjectTree.Kind kind = objectTree.kind;
		if (objectTree.deep < maxDeep) {
			if (kind == ObjectTree.Kind.MULTIVARIABLE_SELF) {
				return;
			}
			List<ObjectTree> children = objectTree.children;
			if (CollectionUtil.nonEmpty(children)) {
				for (ObjectTree child : children) {
					this.inMap(child, map);
				}
				return;
			}
		} else {
			if (kind == ObjectTree.Kind.MULTIVARIABLE_VALUE) {
				return;
			}
		}
		mapValueHandler.handle(objectTree, map);
	}

	protected void tree(ObjectTree objectTree) {
		Object object = objectTree.object;

		// null
		if (Objects.isNull(object)) {
			if (objectTree.kind == ObjectTree.Kind.OTHER) {
				String key = objectTree.key;
				long size = sizeMap.getOrDefault(key, 0L);
				sizeMap.put(key, size + 1L);
				objectTree.index = size;
			}
			return;
		}

		if (directEvaluator.evaluate(object)) {
			return;
		}

		// 指定的 bean
		if (beanEvaluator.evaluate(object)) {
			this.treeWithBean(object, objectTree);
			return;
		}
		// map
		if (object instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) object;
			this.treeWithMap(map, objectTree);
			return;
		}
		// array、iterable、iterator、enumeration
		if (ObjectUtil.isMultivariable(object)) {
			Class<?> componentType = this.getComponentType(object, objectTree.beanProperty);
			if (!BeanUtils.isSimpleValueType(componentType)) {
				Object[] array = ArrayUtil.of(object);
				this.treeWithArray(array, objectTree, componentType);
			}
		}
	}

	protected void treeWithBean(Object bean, ObjectTree objectTree) {
		if (objectTree.kind == ObjectTree.Kind.OTHER) {
			String key = objectTree.key;
			long size = sizeMap.getOrDefault(key, 0L);
			sizeMap.put(key, size + 1L);
			objectTree.index = size;
		}
		Class<?> beanClass = bean.getClass();
		FieldUtil.list(beanClass, true)
			.stream()
			.map(field -> {
				Method method = MethodUtil.GetterOrSetterQuerier.create(MethodUtil.GetterOrSetterQuerier.Mode.GETTER)
					.targetClass(beanClass)
					.field(field)
					.query();
				return new BeanProperty(bean, field, method);
			})
			.forEach(beanProperty -> {
				String name = this.getPropertyName(beanProperty);
				Object value = this.getPropertyValue(beanProperty);
				ObjectTree child = objectTree.child(name, value, beanProperty);
				this.tree(child);
			});
	}

	protected void treeWithMap(Map<?, ?> map, ObjectTree objectTree) {
		if (objectTree.kind == ObjectTree.Kind.OTHER) {
			String key = objectTree.key;
			long size = sizeMap.getOrDefault(key, 0L);
			sizeMap.put(key, size + 1L);
			objectTree.index = size;
		}
		Type typeArgument = TypeReference.getTypeArgument(map.getClass(), 1);
		map.forEach((k, v) -> {
			String name = Objects.toString(k);
			ObjectTree child = objectTree.child(name, v, typeArgument);
			this.tree(child);
		});
	}

	protected void treeWithArray(Object[] array, ObjectTree objectTree, Class<?> componentType) {
		// 修正类型
		objectTree.kind = ObjectTree.Kind.MULTIVARIABLE_SELF;
		String key = objectTree.key;
		long size = sizeMap.getOrDefault(key, 0L);
		int length = array.length;
		sizeMap.put(key, size + length);
		for (int i = 0; i < length; i++) {
			long index = size + i;
			ObjectTree sibling = objectTree.sibling(index, array[i], componentType);
			this.tree(sibling);
		}
	}

	protected Class<?> getComponentType(Object object, BeanProperty beanProperty) {
		if (ArrayUtil.isArray(object)) {
			return ArrayUtil.getComponentType(object);
		}
		Type type = TypeReference.getTypeArgument(object.getClass(), 0);
		Class<?> componentType = ClassUtil.getRawClass(type);
		if (componentType == Object.class && Objects.nonNull(beanProperty)) {
			Type genericType = beanProperty.getField().getGenericType();
			Type typeArgument = TypeReference.getTypeArgument(genericType, 0);
			return ClassUtil.getRawClass(typeArgument);
		}
		return componentType;
	}

	protected String getPropertyName(BeanProperty beanProperty) {
		Option<Field> fieldOpt = Option.of(beanProperty.getField());
		return fieldOpt.map(field -> AnnotationUtils.getAnnotation(field, Param.class))
				.or(() -> Option.of(beanProperty.getGetterMethod())
				.map(method -> AnnotationUtils.getAnnotation(method, Param.class)))
			.map(Param::value)
			.or(() -> fieldOpt.map(Field::getName))
				.getOrNull();
	}

	protected Object getPropertyValue(BeanProperty beanProperty) {
		Object bean = beanProperty.getBean();
		return Option.of(beanProperty.getGetterMethod())
			.map(method -> MethodUtil.invoke(method, bean))
				.or(() -> Option.of(beanProperty.getField())
				.map(field -> FieldUtil.getValue(field, bean)))
				.getOrNull();
	}
}
