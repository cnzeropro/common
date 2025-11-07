package org.zero.common.core.util.java.lang.reflect;

import org.zero.common.core.util.java.lang.ArrayUtil;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.data.constant.StringPool;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Predicate;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/1
 */
public class ExecutableUtil {
	/**
	 * 获取参数类型
	 * <p>
	 * 当参数列表中有参数为 null 时，其类型也为 null（注意 NPE）。
	 *
	 * @param args 参数列表
	 * @return 参数类型列表
	 * @see AvailableParameterTypePredicate
	 */
	public static Class<?>[] getParameterTypes(Object... args) {
		if (ArrayUtil.isEmpty(args)) {
			return new Class[0];
		}
		return Arrays.stream(args)
			.map(o -> Objects.isNull(o) ? null : o.getClass())
			.toArray(Class[]::new);
	}

	/**
	 * 方法或者构造器全限定名（The fully qualified name of method）构造器
	 */
	public static class FQNBuilder {
		public static final String VAR_ARGS_SUFFIX = StringPool.ELLIPSIS;

		protected final Executable executable;
		protected String classMethodSeparator = StringPool.POUND;
		protected String parameterSeparator = StringPool.COMMA;
		protected boolean withReturnType;
		protected boolean dealVarArgs = true;
		protected boolean withModifier;

		protected FQNBuilder(Executable executable) {
			this.executable = executable;
		}

		public static FQNBuilder of(Executable executable) {
			return new FQNBuilder(executable);
		}

		public FQNBuilder classMethodSeparator(String classMethodSeparator) {
			this.classMethodSeparator = classMethodSeparator;
			return this;
		}

		public FQNBuilder parameterSeparator(String parameterSeparator) {
			this.parameterSeparator = parameterSeparator;
			return this;
		}

		public FQNBuilder withReturnType() {
			return this.withReturnType(true);
		}

		public FQNBuilder withReturnType(boolean withReturnType) {
			this.withReturnType = withReturnType;
			return this;
		}

		public FQNBuilder dealVarArgs() {
			return this.dealVarArgs(true);
		}

		public FQNBuilder dealVarArgs(boolean dealVarArgs) {
			this.dealVarArgs = dealVarArgs;
			return this;
		}

		public FQNBuilder withModifier() {
			return this.withModifier(true);
		}

		public FQNBuilder withModifier(boolean withModifier) {
			this.withModifier = withModifier;
			return this;
		}

		public String build() {
			// 修饰符
			String modifier = null;
			if (withModifier) {
				modifier = Modifier.toString(executable.getModifiers());
			}
			// 返回类型
			String returnTypeName = null;
			if (withReturnType) {
				if (executable instanceof Method) {
					returnTypeName = ((Method) executable).getReturnType().getName();
				}
			}
			// 类的全限定名
			String className = executable.getDeclaringClass().getName();
			// 方法名
			String executableName = executable.getName();
			// 参数类型列表（全限定名）
			boolean varArgs = executable.isVarArgs();
			Class<?>[] parameterTypes = executable.getParameterTypes();
			int length = parameterTypes.length;
			StringJoiner stringJoiner = new StringJoiner(parameterSeparator, "(", ")");
			for (int i = 0; i < length; i++) {
				Class<?> parameterType = parameterTypes[i];
				if (varArgs && i == length - 1) {
					if (dealVarArgs) {
						Class<?> componentType = parameterTypes[i].getComponentType();
						stringJoiner.add(componentType.getName() + VAR_ARGS_SUFFIX);
					} else {
						stringJoiner.add(parameterType.getName());
					}
				} else {
					stringJoiner.add(parameterType.getName());
				}
			}
			// 拼接
			StringBuilder stringBuilder = new StringBuilder();
			if (Objects.isNull(modifier)) {
				stringBuilder.append(modifier).append(StringPool.SPACE);
			}
			if (Objects.isNull(returnTypeName)) {
				stringBuilder.append(returnTypeName).append(StringPool.SPACE);
			}
			stringBuilder.append(className)
				.append(classMethodSeparator)
				.append(executableName)
				.append(stringJoiner);
			return stringBuilder.toString();
		}
	}

	/**
	 * 精切参数类型匹配器
	 */
	public static class ExactParameterTypePredicate implements Predicate<Executable> {
		protected final Class<?>[] parameterTypes;

		protected ExactParameterTypePredicate(Class<?>... parameterTypes) {
			this.parameterTypes = parameterTypes;
		}

		public static ExactParameterTypePredicate of(Class<?>... parameterTypes) {
			return new ExactParameterTypePredicate(parameterTypes);
		}

		@Override
		public boolean test(Executable executable) {
			Class<?>[] paramTypes = executable.getParameterTypes();
			if (paramTypes.length != parameterTypes.length) {
				return false;
			}
			// 此处不使用 Arrays.equals 方法，否则无法做到 null 处理
			for (int i = 0; i < paramTypes.length; i++) {
				Class<?> paramType = paramTypes[i];
				Class<?> parameterType = parameterTypes[i];
				// 不为 null 时进行判断处理，否则跳过该次匹配
				if (Objects.nonNull(parameterType)) {
					// 判断参数类型是否相等
					if (!Objects.equals(paramType, parameterType)) {
						return false;
					}
				}
			}
			return true;
		}
	}

	/**
	 * 可用参数类型匹配器（指定 {@link Executable} 的参数分别从传入参数分配而来）
	 */
	public static class AvailableParameterTypePredicate implements Predicate<Executable> {
		protected final Class<?>[] parameterTypes;

		protected AvailableParameterTypePredicate(Class<?>... parameterTypes) {
			this.parameterTypes = parameterTypes;
		}

		public static AvailableParameterTypePredicate of(Class<?>... parameterTypes) {
			return new AvailableParameterTypePredicate(parameterTypes);
		}

		@Override
		public boolean test(Executable executable) {
			Class<?>[] paramTypes = executable.getParameterTypes();
			if (paramTypes.length != parameterTypes.length) {
				return false;
			}
			for (int i = 0; i < paramTypes.length; i++) {
				Class<?> paramType = paramTypes[i];
				Class<?> parameterType = parameterTypes[i];
				// 不为 null 时进行判断处理，否则跳过该次匹配
				if (Objects.nonNull(parameterType)) {
					// 判断参数类型是否兼容
					if (!ClassUtil.isAssignable(paramType, parameterType)) {
						return false;
					}
				}
			}
			return true;
		}
	}

	protected ExecutableUtil() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}
}
