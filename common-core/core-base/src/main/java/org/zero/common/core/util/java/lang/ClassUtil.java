package org.zero.common.core.util.java.lang;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;
import org.zero.common.core.extension.java.lang.reflect.TypeReference;
import org.zero.common.core.extension.java.net.Schemes;
import org.zero.common.core.extension.java.util.function.ThrowableFunction;
import org.zero.common.core.util.java.lang.reflect.ConstructorUtil;
import org.zero.common.core.util.java.lang.reflect.FieldUtil;
import org.zero.common.core.util.java.lang.reflect.MethodUtil;
import org.zero.common.core.util.java.net.UrlUtil;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zero
 * @since 2023/7/17
 */
@Log
@UtilityClass
public class ClassUtil {
	public static final String CLASS_FILE_SUFFIX = ".class";
	public static final String CLASS_FILE_PREFIX = File.separator + "classes" + File.separator;
	public static final String CLASS_FILE_SEPARATOR = File.separator;
	public static final String PACKAGE_SEPARATOR = ".";
	public static final String INNER_CLASS_SEPARATOR = "$";

	/**
	 * 获取包下的所有类
	 *
	 * @param p 包
	 * @return 类对象
	 */
	public static Collection<Class<?>> getClasses(Package p) {
		return getClasses(p.getName());
	}

	/**
	 * 获取包下的所有类
	 *
	 * @param packageName 包名
	 * @return 类对象
	 */
	public static Collection<Class<?>> getClasses(String packageName) {
		return getClassMap(packageName).values()
			.stream()
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	/**
	 * 获取包下的所有类
	 *
	 * @param p 包
	 * @return 类对象
	 */
	public static Map<String, Class<?>> getClassMap(Package p) {
		return getClassMap(p.getName());
	}

	/**
	 * 获取包下的所有类
	 *
	 * @param packageName 包名
	 * @return 类对象
	 */
	public static Map<String, Class<?>> getClassMap(String packageName) {
		Collection<String> classNames = getClassNames(packageName);
		return ClassLoaderUtil.loadClass(classNames);
	}

	/**
	 * 获取包下的所有全限定类名
	 *
	 * @param p 包
	 * @return 全限定类名
	 */
	public static Collection<String> getClassNames(Package p) {
		return getClassNames(p.getName());
	}

	/**
	 * 获取包下的所有全限定类名
	 *
	 * @param packageName 包名
	 * @return 全限定类名
	 */
	public static Collection<String> getClassNames(String packageName) {
		String packageFileName = packageName.replace(PACKAGE_SEPARATOR, CLASS_FILE_SEPARATOR);
		Enumeration<URL> urls = ThrowableUtil.sneakyThrow(ClassLoaderUtil.get(),
			(ThrowableFunction<ClassLoader, Enumeration<URL>>) classLoader -> classLoader.getResources(packageFileName));
		return Collections.list(urls)
			.stream()
			.map(url -> getClassNamesByUrl(url, packageName))
			.flatMap(Collection::stream)
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public static Collection<String> getClassNamesByUrl(URL url, String packageName) {
		return getClassNamesByUrl(url, packageName, true, false);
	}

	public static Collection<String> getClassNamesByUrl(URL url, String packageName, boolean withSubordinateClass, boolean withInnerClass) {
		String protocol = url.getProtocol();
		if (Schemes.FILE.equalsIgnoreCase(protocol)) {
			return ThrowableUtil.sneakyThrowOpt(url, (ThrowableFunction<URL, URI>) URL::toURI)
				.map(File::new)
				.map(file -> getClassNamesByFile(file, packageName, withSubordinateClass, withInnerClass))
				.orElseGet(LinkedHashSet::new);
		}
		if (Schemes.JAR.equalsIgnoreCase(protocol)) {
			return Optional.ofNullable(UrlUtil.openConnection(url))
				.filter(JarURLConnection.class::isInstance)
				.map(JarURLConnection.class::cast)
				.map(ThrowableUtil.sneakyThrow(JarURLConnection::getJarFile))
				.map(jarFile -> getClassNamesByJar(jarFile, packageName, withSubordinateClass, withInnerClass))
				.orElseGet(LinkedHashSet::new);
		}
		return Collections.emptyList();
	}

	public static Collection<String> getClassNamesByFile(File file, String packageName) {
		return getClassNamesByFile(file, packageName, true, false);
	}

	public static Collection<String> getClassNamesByFile(File file, String packageName, boolean withSubordinateClass, boolean withInnerClass) {
		return Optional.ofNullable(file)
			.filter(File::exists)
			.filter(f -> withSubordinateClass || !f.isDirectory())
			.<Collection<String>>map(f -> {
				if (f.isFile()) {
					return Optional.of(f.getPath())
						.filter(filePath -> withInnerClass || !filePath.contains(INNER_CLASS_SEPARATOR))
						.filter(filePath -> filePath.endsWith(CLASS_FILE_SUFFIX) && filePath.contains(CLASS_FILE_PREFIX))
						.map(filePath -> filePath.substring(filePath.indexOf(CLASS_FILE_PREFIX) + CLASS_FILE_PREFIX.length())
							.replace(CLASS_FILE_SEPARATOR, PACKAGE_SEPARATOR)
							.replace(CLASS_FILE_SUFFIX, ""))
						.filter(className -> {
							if (withSubordinateClass) {
								return className.startsWith(packageName);
							}
							return packageName.equals(className.substring(0, className.lastIndexOf(PACKAGE_SEPARATOR)));
						})
						.<Collection<String>>map(Collections::singletonList)
						.orElseGet(LinkedHashSet::new);
				}
				if (f.isDirectory()) {
					return Optional.ofNullable(f.listFiles())
						.map(Arrays::stream)
						.orElseGet(Stream::empty)
						.map(nextFile -> getClassNamesByFile(nextFile, packageName, withSubordinateClass, withInnerClass))
						.flatMap(Collection::stream)
						.collect(Collectors.toCollection(LinkedHashSet::new));
				}
				return Collections.emptyList();
			})
			.orElseGet(LinkedHashSet::new);
	}

	public static Collection<String> getClassNamesByJar(JarFile jarFile, String packageName) {
		return getClassNamesByJar(jarFile, packageName, true, false);
	}

	public static Collection<String> getClassNamesByJar(JarFile jarFile, String packageName, boolean withSubordinateClass, boolean withInnerClass) {
		return Optional.ofNullable(jarFile)
			.map(JarFile::stream)
			.orElseGet(Stream::empty)
			.map(JarEntry::getName)
			.filter(entryName -> entryName.endsWith(CLASS_FILE_SUFFIX))
			.map(entryName -> entryName.replace(CLASS_FILE_SUFFIX, "").replace(CLASS_FILE_SEPARATOR, PACKAGE_SEPARATOR))
			.filter(className -> withInnerClass || !className.contains(INNER_CLASS_SEPARATOR))
			.filter(className -> {
				if (withSubordinateClass) {
					return className.startsWith(packageName);
				}
				return packageName.equals(className.substring(0, className.lastIndexOf(PACKAGE_SEPARATOR)));
			})
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	/**
	 * 是否是指定正则匹配的类
	 */
	public static boolean isClassWithRegex(Class<?> clazz, String... regexps) {
		return Optional.ofNullable(clazz)
			.map(Class::getCanonicalName)
			.map(name -> {
				for (String regexp : regexps) {
					if (Pattern.matches(regexp, name)) {
						return true;
					}
				}
				return false;
			})
			.orElse(Boolean.FALSE);
	}

	/**
	 * 是否是指定正则匹配的类
	 */
	public static boolean isClassWithRegex(Class<?> clazz, Pattern... patterns) {
		return Optional.ofNullable(clazz)
			.map(Class::getCanonicalName)
			.map(name -> {
				for (Pattern pattern : patterns) {
					if (pattern.matcher(name).matches()) {
						return true;
					}
				}
				return false;
			})
			.orElse(Boolean.FALSE);
	}

	/**
	 * 是否是指定前缀的类
	 */
	public static boolean isClassWithPrefix(Class<?> clazz, String... prefixes) {
		return Optional.ofNullable(clazz)
			.map(Class::getCanonicalName)
			.map(name -> {
				for (String prefix : prefixes) {
					if (name.startsWith(prefix)) {
						return true;
					}
				}
				return false;
			})
			.orElse(Boolean.FALSE);
	}

	/**
	 * 是否是指定包下的类
	 */
	public static boolean isClassWithPackageName(Class<?> clazz, String... packageNames) {
		return Optional.ofNullable(clazz)
			.map(Class::getPackage)
			.map(Package::getName)
			.map(name -> {
				for (String packageName : packageNames) {
					if (Objects.equals(name, packageName)) {
						return true;
					}
				}
				return false;
			})
			.orElse(Boolean.FALSE);
	}

	/**
	 * 是否是严格的 Java 规范定义 Bean 类型
	 */
	public static boolean isJavaStrictBean(Class<?> clazz) {
		// null 验证
		if (Objects.isNull(clazz)) {
			return false;
		}
		// 类型验证
		if (clazz.isInterface()) {
			return false;
		}
		if (clazz.isArray()) {
			return false;
		}
		if (clazz.isPrimitive()) {
			return false;
		}
		if (clazz.isEnum()) {
			return false;
		}
		if (isNumber(clazz)) {
			return false;
		}
		if (isDateTime(clazz)) {
			return false;
		}
		// 类名验证（排除 Java 官方包）
		if (isClassWithPrefix(clazz, "java", "javax", "jdk", "sun", "com.sun", "jakarta")) {
			return false;
		}
		// 继承验证（必须实现自 Serializable）
		if (!Serializable.class.isAssignableFrom(clazz)) {
			return false;
		}
		// 构造函数验证（必须存在公共无参构造函数）
		if (!ConstructorUtil.getOptByParam(clazz)
			.filter(constructor -> Modifier.isPublic(constructor.getModifiers()))
			.isPresent()) {
			return false;
		}
		// 字段验证
		Collection<Field> fields = FieldUtil.listFiltered(clazz, field -> {
			int mod = field.getModifiers();
			// 不是父类引用字段
			return !FieldUtil.isOuterClassField(field) &&
				// 不是静态字段
				!Modifier.isStatic(mod) &&
				// 是私有字段
				Modifier.isPrivate(mod) &&
				// 不是合成字段
				!field.isSynthetic();
		});
		if (fields.isEmpty()) {
			return false;
		}
		// 方法验证（必须存在 Public 的 getter 和 setter 方法）
		Map<String, Method> methodMap = MethodUtil.listPublic(clazz)
			.stream()
			.collect(Collectors.toMap(Method::getName, Function.identity()));
		boolean hasGetter = fields.stream()
			.map(MethodUtil::getGetterNameByField)
			.allMatch(name -> Objects.nonNull(methodMap.get(name)));
		boolean hasSetter = fields.stream()
			.map(MethodUtil::getSetterNameByField)
			.allMatch(name -> Objects.nonNull(methodMap.get(name)));
		return hasGetter && hasSetter;
	}


	/**
	 * 是否是数字类型
	 */
	public static boolean isNumber(Class<?> clazz) {
		return Objects.nonNull(clazz) &&
			(Number.class.isAssignableFrom(clazz) ||
				(clazz.isPrimitive() &&
					(clazz == int.class || clazz == long.class ||
						clazz == short.class || clazz == byte.class ||
						clazz == float.class || clazz == double.class)));
	}

	/**
	 * 是否是日期时间类型
	 */
	public static boolean isDateTime(Class<?> clazz) {
		return Objects.nonNull(clazz) &&
			(Date.class.isAssignableFrom(clazz) ||
				Calendar.class.isAssignableFrom(clazz) ||
				TemporalAccessor.class.isAssignableFrom(clazz));
	}

	/**
	 * 是否是数组类型
	 */
	public static boolean isArray(Class<?> clazz) {
		return Objects.nonNull(clazz) && clazz.isArray();
	}

	public static boolean isMultivariable(Class<?> clazz) {
		return Objects.nonNull(clazz) &&
			(isArray(clazz) ||
				Iterable.class.isAssignableFrom(clazz) ||
				Iterator.class.isAssignableFrom(clazz) ||
				Enumeration.class.isAssignableFrom(clazz));
	}

	/**
	 * 将对象转换为指定类型对象
	 *
	 * @param obj  对象
	 * @param type 类型
	 * @param <T>  泛型类型
	 * @return 转换后的对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj, Type type) {
		if (Objects.isNull(obj)) {
			return null;
		}
		if (type instanceof Class) {
			Class<T> clazz = (Class<T>) type;
			return clazz.cast(obj);
		}
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type rawType = parameterizedType.getRawType();
			return cast(obj, rawType);
		}
		if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			Type componentType = genericArrayType.getGenericComponentType();
			Object[] inputArray = (Object[]) obj;
			int length = inputArray.length;
			Class<?> componentClass = getRawClass(componentType);
			Object outputArray = Array.newInstance(componentClass, length);
			for (int i = 0; i < length; i++) {
				Array.set(outputArray, i, cast(inputArray[i], componentType));
			}
			return (T) outputArray;
		}
		if (type instanceof TypeVariable) {
			TypeVariable<?> typeVariable = (TypeVariable<?>) type;
			Type[] bounds = typeVariable.getBounds();
			Type boundType = bounds.length > 0 ? bounds[0] : Object.class;
			return cast(obj, boundType);
		}
		if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			Type[] upperBounds = wildcardType.getUpperBounds();
			if (upperBounds.length > 0 && upperBounds[0] != Object.class) {
				return cast(obj, upperBounds[0]);
			}
			Type[] lowerBounds = wildcardType.getLowerBounds();
			if (lowerBounds.length > 0) {
				return cast(obj, lowerBounds[0]);
			}
		}
		if (type instanceof TypeReference) {
			TypeReference<T> typeReference = (TypeReference<T>) type;
			Type referenceType = typeReference.getType();
			return cast(obj, referenceType);
		}
		return (T) obj;
	}

	/**
	 * 获取原始类型
	 *
	 * @param type 类型
	 * @return 原始类型
	 */
	public static Class<?> getRawClass(Type type) {
		if (type == null) {
			return null;
		}
		if (type instanceof Class) {
			return (Class<?>) type;
		}
		if (type instanceof ParameterizedType) {
			return getRawClass(((ParameterizedType) type).getRawType());
		}
		if (type instanceof GenericArrayType) {
			Class<?> componentClass = getRawClass(((GenericArrayType) type).getGenericComponentType());
			return Array.newInstance(componentClass, 0).getClass();
		}
		if (type instanceof TypeVariable) {
			Type[] bounds = ((TypeVariable<?>) type).getBounds();
			return bounds.length > 0 ? getRawClass(bounds[0]) : Object.class;
		}
		return Object.class;
	}

	/**
	 * 检查目标类型是否从源类型分配而来<br>
	 * 包括：<br>
	 * 1、原类是对象，目标类型是源类型实现的接口<br>
	 * 2、目标类型是源类型的父类<br>
	 * 3、两者是原始类型或者包装类型（相互转换）
	 *
	 * @param targetClass 目标类型，如：int.class
	 * @param sourceClass 源类型，如：Integer.class
	 * @return 是否可转化
	 */
	public static boolean isAssignable(Class<?> targetClass, Class<?> sourceClass) {
		if (Objects.isNull(targetClass) || Objects.isNull(sourceClass)) {
			return false;
		}
		// 对象类型
		if (targetClass.isAssignableFrom(sourceClass)) {
			return true;
		}
		// 基本类型
		if (targetClass.isPrimitive()) {
			Class<?> resolvedPrimitive = PrimitiveType.unwrap(sourceClass);
			return targetClass.equals(resolvedPrimitive);
		}
		// 包装类型
		Class<?> resolvedWrapper = PrimitiveType.wrap(sourceClass);
		return Objects.nonNull(resolvedWrapper) && targetClass.isAssignableFrom(resolvedWrapper);
	}

	/**
	 * 检查目标类型是否可以从源类型转化
	 * <p>
	 * 注意：该方法支持 Java 中可强转的和可以相互兼容的类型，因此当该方法返回 true，不一定可强转。
	 *
	 * @param targetType 目标类型，如：int[].class
	 * @param sourceType 源类型，如：Integer[].class
	 * @return 是否可以转化
	 */
	public static boolean isConvertible(Type targetType, Type sourceType) {
		if (Objects.isNull(targetType) || Objects.isNull(sourceType)) {
			return false;
		}
		if (targetType.equals(sourceType)) {
			return true;
		}
		if (targetType instanceof Class) {
			Class<?> targetClass = (Class<?>) targetType;
			if (sourceType instanceof Class) {
				Class<?> sourceClass = (Class<?>) sourceType;
				// 数组类型
				if (targetClass.isArray() && sourceClass.isArray()) {
					return isConvertible(targetClass.getComponentType(), sourceClass.getComponentType());
				}
				return isAssignable(targetClass, sourceClass);
			}
			if (sourceType instanceof GenericArrayType && targetClass.isArray()) {
				GenericArrayType sourceGenericArrayType = (GenericArrayType) sourceType;
				return isConvertible(targetClass.getComponentType(), sourceGenericArrayType.getGenericComponentType());
			}
			if (sourceType instanceof ParameterizedType) {
				ParameterizedType sourceParameterizedType = (ParameterizedType) sourceType;
				return isConvertible(targetClass, sourceParameterizedType.getRawType());
			}
			return false;
		}
		if (targetType instanceof ParameterizedType) {
			ParameterizedType targetParameterizedType = (ParameterizedType) targetType;
			if (sourceType instanceof ParameterizedType) {
				ParameterizedType sourceParameterizedType = (ParameterizedType) sourceType;
				Type targetRawType = targetParameterizedType.getRawType();
				Type sourceRawType = sourceParameterizedType.getRawType();
				if (!isConvertible(targetRawType, sourceRawType)) {
					return false;
				}
				Type[] targetActualTypeArguments = targetParameterizedType.getActualTypeArguments();
				Type[] sourceActualTypeArguments = sourceParameterizedType.getActualTypeArguments();
				if (targetActualTypeArguments.length != sourceActualTypeArguments.length) {
					return false;
				}
				for (int i = 0; i < targetActualTypeArguments.length && i < sourceActualTypeArguments.length; i++) {
					Type targetActualTypeArgument = targetActualTypeArguments[i];
					Type sourceActualTypeArgument = sourceActualTypeArguments[i];
					if (!isConvertible(targetActualTypeArgument, sourceActualTypeArgument)) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
		if (targetType instanceof GenericArrayType) {
			GenericArrayType targetGenericArrayType = (GenericArrayType) targetType;
			if (sourceType instanceof Class) {
				Class<?> sourceClass = (Class<?>) sourceType;
				if (sourceClass.isArray()) {
					return isConvertible(targetGenericArrayType.getGenericComponentType(), sourceClass.getComponentType());
				}
				return false;
			}
			if (sourceType instanceof GenericArrayType) {
				GenericArrayType sourceGenericArrayType = (GenericArrayType) sourceType;
				return isConvertible(targetGenericArrayType.getGenericComponentType(), sourceGenericArrayType.getGenericComponentType());
			}
			return false;
		}
		return false;
	}

	/**
	 * 获取指定类型的默认值
	 *
	 * @param clazz 类型
	 * @return 默认值
	 */
	public static Object getDefaultValue(Class<?> clazz) {
		if (Objects.isNull(clazz)) {
			return null;
		}
		// 原始类型
		if (clazz.isPrimitive()) {
			return PrimitiveType.getOptByPrimitiveClass(clazz)
				.map(PrimitiveType::getDefaultValue)
				.orElse(null);
		}
		// 引用类型
		return null;
	}

	public static boolean has(CharSequence className) {
		return ClassLoaderUtil.loadClassOpt(className, false).isPresent();
	}

	public static final String UNKNOWN_VERSION = "UNKNOWN";

	public static String getImplementationVersion(Class<?> clazz) {
		try {
			String implementationVersion = clazz.getPackage().getImplementationVersion();
			if (Objects.nonNull(implementationVersion)) {
				return implementationVersion;
			}
			CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
			if (Objects.isNull(codeSource)) {
				return UNKNOWN_VERSION;
			}
			URL codeSourceLocation = codeSource.getLocation();
			URLConnection connection = codeSourceLocation.openConnection();
			if (connection instanceof JarURLConnection) {
				return getImplementationVersion(((JarURLConnection) connection).getJarFile());
			}
			final File file = new File(codeSourceLocation.toURI());
			if (!file.exists() || file.isDirectory()) {
				return UNKNOWN_VERSION;
			}
			try (JarFile jarFile = new JarFile(file)) {
				return getImplementationVersion(jarFile);
			}
		} catch (Throwable t) {
			log.log(Level.WARNING, "Failed to implementation version from " + clazz, t);
			return UNKNOWN_VERSION;
		}
	}

	@SneakyThrows
	public static String getImplementationVersion(JarFile jarFile) {
		return Optional.ofNullable(jarFile)
			.map(ThrowableUtil.sneakyThrow(JarFile::getManifest))
			.map(Manifest::getMainAttributes)
			.map(mainAttributes -> mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION))
			.orElse(UNKNOWN_VERSION);
	}
}
