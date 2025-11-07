package org.zero.common.core.util.java.lang.reflect;

import org.zero.common.core.util.java.lang.ArrayUtil;
import org.zero.common.core.util.java.lang.ClassUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/1
 */
public class MemberUtil {
	/**
	 * 访问权限比较器（按访问权限从高到低排序：public → protected → default → private）
	 */
	public static final Comparator<Member> ACCESSIBLE_COMPARATOR = Comparator.comparingInt(member -> {
		int mod = member.getModifiers();
		return Modifier.isPublic(mod) ? 0 // public
			: Modifier.isProtected(mod) ? 1 // protected
			: Modifier.isPrivate(mod) ? 3 // private
			: 2; // default
	});

	public static boolean isStatic(Member member) {
		if (Objects.isNull(member)) {
			return false;
		}
		return Modifier.isStatic(member.getModifiers());
	}

	public static boolean isFinal(Member member) {
		if (Objects.isNull(member)) {
			return false;
		}
		return Modifier.isFinal(member.getModifiers());
	}

	/**
	 * 获取实例
	 *
	 * @param clazz 类对象
	 * @param <T>   类型
	 * @return 实例
	 */
	public static <T> T getInstance(Class<T> clazz) {
		return getInstance(clazz, (Object[]) null);
	}

	/**
	 * 获取实例
	 *
	 * @param clazz 类对象
	 * @param args  参数列表
	 * @param <T>   类型
	 * @return 实例
	 */
	public static <T> T getInstance(Class<T> clazz, Object... args) {
		return getInstance(clazz, false, args);
	}

	/**
	 * 获取实例
	 *
	 * @param clazz            类对象
	 * @param quietIfException 是否安静处理（不抛出异常）
	 * @param <T>              类型
	 * @return 实例
	 */
	public static <T> T getInstance(Class<T> clazz, boolean quietIfException) {
		return getInstanceOpt(clazz, quietIfException, (Object[]) null).orElse(null);
	}

	/**
	 * 获取实例
	 *
	 * @param clazz            类对象
	 * @param quietIfException 是否安静处理（不抛出异常）
	 * @param args             参数列表
	 * @param <T>              类型
	 * @return 实例
	 */
	public static <T> T getInstance(Class<T> clazz, boolean quietIfException, Object... args) {
		return getInstanceOpt(clazz, quietIfException, args).orElse(null);
	}

	/**
	 * 获取实例 {@link Optional}
	 *
	 * @param clazz 类对象
	 * @param <T>   类型
	 * @return 实例 {@link Optional}
	 */
	public static <T> Optional<T> getInstanceOpt(Class<T> clazz) {
		return getInstanceOpt(clazz, (Object[]) null);
	}

	/**
	 * 获取实例 {@link Optional}
	 *
	 * @param clazz 类对象
	 * @param args  参数列表
	 * @param <T>   类型
	 * @return 实例 {@link Optional}
	 */
	public static <T> Optional<T> getInstanceOpt(Class<T> clazz, Object... args) {
		return getInstanceOpt(clazz, true, args);
	}

	/**
	 * 获取实例 {@link Optional}
	 *
	 * @param clazz            类对象
	 * @param quietIfException 是否安静处理（不抛出异常）
	 * @param <T>              类型
	 * @return 实例 {@link Optional}
	 */
	public static <T> Optional<T> getInstanceOpt(Class<T> clazz, boolean quietIfException) {
		return getInstanceOpt(clazz, quietIfException, (Object[]) null);
	}

	/**
	 * 获取实例 {@link Optional}
	 * <p>
	 * 分别尝试从静态方法、静态字段、构造器中获取类实例 {@link Optional}。<br>
	 * 尝试顺序如下：
	 * <ul>
	 *     <li>先按访问权限排序，优先级：public > protected > default > private</li>
	 *     <li>再按类型排序，优先级：Field > Method > Constructor</li>
	 *     <li>最后按参数排序，优先级：当存在传入的参数（不为 null）时，以其为主（完全匹配 > 兼容匹配 > 参数个数匹配 > 其他情况）；不存在时，以方法参数个数为主（个数越小优先级越高）</li>
	 * </ul>
	 * <b>注意：当参数列表不为 null 时，只能尝试从静态方法、构造器获取类实例 {@link Optional}。</b>
	 *
	 * @param clazz            类对象
	 * @param quietIfException 是否安静处理（不抛出异常）
	 * @param args             参数列表
	 * @param <T>              类型
	 * @return 实例 {@link Optional}
	 */
	public static <T> Optional<T> getInstanceOpt(Class<T> clazz, boolean quietIfException, Object... args) {
		if (Objects.isNull(clazz)) {
			return Optional.empty();
		}
		List<Member> members = new ArrayList<>();
		// 如果参数列表为空，则尝试从静态字段获取
		if (ArrayUtil.isEmpty(args)) {
			Collection<Field> fields = listInstanceField(clazz);
			members.addAll(fields);
		}
		// 获取参数类型列表
		Class<?>[] parameterTypes = ExecutableUtil.getParameterTypes(args);
		// 从静态方法获取
		Collection<Method> methods = listInstanceMethod(clazz, parameterTypes);
		members.addAll(methods);
		// 从构造器获取
		Collection<Constructor<T>> constructors = listInstanceConstructor(clazz, parameterTypes);
		members.addAll(constructors);
		// 先按访问权限排序，优先级：public > protected > default > private
		// 再按类型排序，优先级：Field > Method > Constructor
		// 最后按参数排序，优先级：当存在传入的参数（不为 null）时，以其为主（完全匹配 > 兼容匹配 > 参数个数匹配 > 其他情况）；不存在时，以方法参数个数为主（个数越小优先级越高）
		Comparator<Member> comparator = ACCESSIBLE_COMPARATOR.thenComparingInt(member -> member instanceof Field ? 0
				: member instanceof Method ? 1
				: member instanceof Constructor ? 2
				: 4)
			.thenComparingInt(member -> {
				if (Objects.isNull(args)) {
					return member instanceof Method ? ((Method) member).getParameterCount()
						: member instanceof Constructor ? ((Constructor<?>) member).getParameterCount()
						: Integer.MAX_VALUE;
				}
				if (member instanceof Executable) {
					Executable executable = (Executable) member;
					if (ExecutableUtil.ExactParameterTypePredicate.of(parameterTypes).test(executable)) {
						return 0;
					}
					if (ExecutableUtil.AvailableParameterTypePredicate.of(parameterTypes).test(executable)) {
						return 1;
					}
					Class<?>[] paramTypes = executable.getParameterTypes();
					if (paramTypes.length == parameterTypes.length) {
						return 2;
					}
					return 3;
				}
				return Integer.MAX_VALUE;
			});
		// 排序
		members.sort(comparator);
		// 尝试实例化
		for (Member member : members) {
			Object obj = null;
			if (member instanceof Field) {
				obj = FieldUtil.getStaticValue((Field) member, quietIfException);
			} else if (member instanceof Method) {
				Method method = (Method) member;
				Object[] parameters = args;
				if (ArrayUtil.isEmpty(args)) {
					parameters = Arrays.stream(method.getParameterTypes())
						.map(ClassUtil::getDefaultValue)
						.toArray();
				}
				obj = MethodUtil.invokeStatic(method, quietIfException, parameters);
			} else if (member instanceof Constructor) {
				Constructor<?> constructor = (Constructor<?>) member;
				Object[] parameters = args;
				if (ArrayUtil.isEmpty(args)) {
					parameters = Arrays.stream(constructor.getParameterTypes())
						.map(ClassUtil::getDefaultValue)
						.toArray();
				}
				obj = ConstructorUtil.newInstance(constructor, quietIfException, parameters);
			}
			if (Objects.nonNull(obj)) {
				T instance = ClassUtil.cast(obj, clazz);
				return Optional.ofNullable(instance);
			}
		}
		return Optional.empty();
	}

	/**
	 * 获取实例字段
	 * <p>
	 * 实例字段要求：
	 * <ol>
	 *     <li>静态字段</li>
	 *     <li>在本类</li>
	 *     <li>字段类型从本类分配而来</li>
	 * </ol>
	 */
	protected static <T> Collection<Field> listInstanceField(Class<T> clazz) {
		return FieldUtil.listStatic(clazz, false)
			.stream()
			.filter(field -> ClassUtil.isAssignable(field.getType(), clazz))
			.collect(Collectors.toList());
	}

	/**
	 * 获取实例化方法（构建方法）
	 * <p>
	 * 构建方法要求：
	 * <ol>
	 *     <li>静态方法</li>
	 *     <li>在本类</li>
	 *     <li>方法返回类型从本类分配而来</li>
	 * </ol>
	 */
	protected static <T> Collection<Method> listInstanceMethod(Class<T> clazz, Class<?>... parameterTypes) {
		Stream<Method> methodStream = MethodUtil.listStatic(clazz, false)
			.stream()
			.filter(MethodUtil::isBuilderMethod);
		if (ArrayUtil.nonEmpty(parameterTypes)) {
			methodStream = methodStream.filter(ExecutableUtil.AvailableParameterTypePredicate.of(parameterTypes));
		}
		return methodStream.collect(Collectors.toList());
	}

	/**
	 * 获取实例构造器
	 */
	@SuppressWarnings("unchecked")
	protected static <T> Collection<Constructor<T>> listInstanceConstructor(Class<T> clazz, Class<?>... parameterTypes) {
		Stream<Constructor<?>> constructorStream = ConstructorUtil.list(clazz, false).stream();
		if (ArrayUtil.nonEmpty(parameterTypes)) {
			constructorStream = constructorStream.filter(ExecutableUtil.AvailableParameterTypePredicate.of(parameterTypes));
		}
		return (Collection<Constructor<T>>) (Object) constructorStream.collect(Collectors.toList());
	}
}
