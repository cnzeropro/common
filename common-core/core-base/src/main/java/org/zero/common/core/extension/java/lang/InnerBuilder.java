package org.zero.common.core.extension.java.lang;

import org.zero.common.core.util.java.lang.reflect.AccessibleObjectUtil;
import org.zero.common.core.util.java.lang.reflect.ConstructorUtil;
import org.zero.common.core.util.java.lang.reflect.FieldUtil;
import org.zero.common.core.util.java.lang.reflect.MethodUtil;
import org.zero.common.data.exception.CommonException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 通用内部构建器。
 * <p>
 * 默认实例化策略如下：
 * <ol>
 *     <li>优先按“目标类字段声明顺序 = 构造器参数顺序”自动推导构造器元数据。</li>
 *     <li>如果默认推导找不到匹配构造器，则回退到无参构造 + setter 绑定。</li>
 * </ol>
 * 如果目标类的构造器顺序与字段顺序不一致，子类可以覆写
 * {@link #constructorParameterTypes()} 和 {@link #constructorArguments()}
 * 显式指定构造器元数据。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/29
 */
public abstract class InnerBuilder<A, B extends InnerBuilder<A, B>> implements Builder<A, B> {
	private static final Class<?>[] EMPTY_PARAMETER_TYPES = new Class<?>[0];
	private static final Object[] EMPTY_ARGUMENTS = new Object[0];
	private static final String CONSTRUCTOR_PARAMETER_TYPES_METHOD = "constructorParameterTypes";
	private static final String CONSTRUCTOR_ARGUMENTS_METHOD = "constructorArguments";

	@Override
	public A build() {
		A obj = this.instantiate();
		this.validate(obj);
		return obj;
	}

	/**
	 * 实例化目标对象。
	 * <p>
	 * 只有“默认自动推导但找不到构造器”这一种场景允许回退到 setter 路径。
	 * 如果子类显式提供了构造元数据，则找不到构造器会直接报错；
	 * 如果构造器本身执行失败，也会直接透传异常，不再回退。
	 */
	@SuppressWarnings("unchecked")
	protected A instantiate() {
		Class<?> targetClass = this.targetClass();
		Class<?>[] parameterTypes = this.normalizedConstructorParameterTypes();
		Object[] arguments = this.normalizedConstructorArguments();

		this.validateConstructorMetadata(targetClass, parameterTypes, arguments);
		if (parameterTypes.length == 0) {
			return this.instantiateWithSetters(targetClass);
		}

		Constructor<A> constructor = (Constructor<A>) ConstructorUtil.getOptByParam(targetClass, parameterTypes).orElse(null);
		if (Objects.nonNull(constructor)) {
			return ConstructorUtil.newInstance(constructor, arguments);
		}
		if (this.hasExplicitConstructorMetadata()) {
			throw this.missingConstructorException(targetClass, parameterTypes);
		}
		return this.instantiateWithSetters(targetClass);
	}

	/**
	 * 获取目标类。
	 * <p>
	 * 默认要求 builder 是目标类的内部类。
	 */
	protected Class<?> targetClass() {
		Class<?> builderClass = this.getClass();
		Class<?> targetClass = builderClass.getEnclosingClass();
		if (Objects.isNull(targetClass)) {
			throw new CommonException(String.format("%s must have enclosing class", builderClass));
		}
		return targetClass;
	}

	/**
	 * 构造器参数类型。
	 * <p>
	 * 默认按 {@link #constructorFields()} 自动推导；特殊场景下子类可覆写。
	 */
	protected Class<?>[] constructorParameterTypes() {
		return this.constructorFields().stream()
				.map(Field::getType)
				.toArray(Class<?>[]::new);
	}

	/**
	 * 构造器参数值。
	 * <p>
	 * 默认按 {@link #constructorFields()} 的字段名从 builder 中取值；
	 * 特殊场景下子类可覆写。
	 */
	protected Object[] constructorArguments() {
		return this.constructorFields().stream()
				.map(Field::getName)
				.map(this::requireBuilderField)
				.map(field -> FieldUtil.getValue(field, this))
				.toArray(Object[]::new);
	}

	/**
	 * 构造器自动推导使用的字段。
	 * <p>
	 * 默认只看目标类当前声明的实例字段，并要求 builder 中存在同名字段。
	 * 这样可以尽量减少对派生字段、运行时字段的误绑定。
	 */
	protected Collection<Field> constructorFields() {
		return this.targetFields(false).stream()
				.filter(field -> this.findBuilderField(field.getName()).isPresent())
				.collect(Collectors.toList());
	}

	/**
	 * setter 绑定使用的字段。
	 * <p>
	 * 默认只绑定 builder 中存在、且目标类或父类中也存在同名字段的实例字段。
	 */
	protected Collection<Field> bindableFields() {
		return this.builderFields().stream()
				.filter(field -> this.hasTargetField(field.getName(), true))
				.collect(Collectors.toList());
	}

	protected void validate(A obj) {
		// default: do nothing
	}

	protected Collection<Field> builderFields() {
		return this.instanceFields(this.getClass(), true);
	}

	protected Collection<Field> targetFields(boolean withSuperClassFields) {
		return this.instanceFields(this.targetClass(), withSuperClassFields);
	}

	protected Optional<Field> findBuilderField(CharSequence fieldName) {
		return FieldUtil.getOptByName(this.getClass(), true, fieldName)
				.filter(this::isInstanceField);
	}

	protected Field requireBuilderField(CharSequence fieldName) {
		return this.findBuilderField(fieldName)
				.orElseThrow(() -> new CommonException(String.format("%s has no builder field named %s",
						this.getClass().getName(),
						fieldName)));
	}

	protected boolean hasTargetField(CharSequence fieldName, boolean withSuperClassFields) {
		return FieldUtil.getOptByName(this.targetClass(), withSuperClassFields, fieldName)
				.filter(this::isInstanceField)
				.isPresent();
	}

	protected boolean hasExplicitConstructorMetadata() {
		return this.isMethodOverridden(CONSTRUCTOR_PARAMETER_TYPES_METHOD)
				|| this.isMethodOverridden(CONSTRUCTOR_ARGUMENTS_METHOD);
	}

	protected CommonException missingConstructorException(Class<?> targetClass, Class<?>[] parameterTypes) {
		return new CommonException(String.format("%s has no constructor with parameter types %s",
				targetClass.getName(),
				Arrays.toString(parameterTypes)));
	}

	protected Collection<Field> instanceFields(Class<?> type, boolean withSuperClassFields) {
		Collection<Field> fields = FieldUtil.listFiltered(type, withSuperClassFields, this::isInstanceField);
		return Objects.isNull(fields) ? Collections.emptyList() : fields;
	}

	protected boolean isInstanceField(Field field) {
		return !Modifier.isStatic(field.getModifiers()) && !field.isSynthetic();
	}

	protected boolean isMethodOverridden(String methodName) {
		Class<?> searchType = this.getClass();
		while (Objects.nonNull(searchType) && !InnerBuilder.class.equals(searchType)) {
			try {
				searchType.getDeclaredMethod(methodName);
				return true;
			} catch (NoSuchMethodException ignored) {
				searchType = searchType.getSuperclass();
			}
		}
		return false;
	}

	protected Class<?>[] normalizedConstructorParameterTypes() {
		Class<?>[] parameterTypes = this.constructorParameterTypes();
		return Objects.isNull(parameterTypes) ? EMPTY_PARAMETER_TYPES : parameterTypes;
	}

	protected Object[] normalizedConstructorArguments() {
		Object[] arguments = this.constructorArguments();
		return Objects.isNull(arguments) ? EMPTY_ARGUMENTS : arguments;
	}

	protected void validateConstructorMetadata(Class<?> targetClass, Class<?>[] parameterTypes, Object[] arguments) {
		if (parameterTypes.length != arguments.length) {
			throw new CommonException(String.format("%s constructor metadata length mismatch: parameterTypes=%d, arguments=%d",
					targetClass.getName(),
					parameterTypes.length,
					arguments.length));
		}
	}

	@SuppressWarnings("unchecked")
	protected A instantiateWithSetters(Class<?> targetClass) {
		Constructor<A> constructor = (Constructor<A>) ConstructorUtil.getOptByParam(targetClass, EMPTY_PARAMETER_TYPES).orElse(null);
		if (Objects.isNull(constructor)) {
			throw new CommonException(String.format("%s has no no-args constructor", targetClass.getName()));
		}

		A obj = ConstructorUtil.newInstance(constructor);
		for (Field field : this.bindableFields()) {
			Method setter = this.requireSetter(targetClass, field);
			this.invokeSetter(setter, obj, FieldUtil.getValue(field, this));
		}
		return obj;
	}

	protected Method requireSetter(Class<?> targetClass, Field field) {
		return MethodUtil.getOptByNameAndParam(targetClass, true, MethodUtil.getSetterNameByField(field), field.getType())
				.orElseThrow(() -> new CommonException(String.format("%s has no setter for field %s",
						targetClass.getName(),
						field.getName())));
	}

	protected void invokeSetter(Method setter, A obj, Object value) {
		boolean accessible = setter.isAccessible();
		if (!accessible) {
			AccessibleObjectUtil.setAccessible(setter);
		}
		try {
			MethodUtil.invoke(setter, obj, value);
		} finally {
			if (!accessible) {
				AccessibleObjectUtil.setInaccessible(setter);
			}
		}
	}
}
