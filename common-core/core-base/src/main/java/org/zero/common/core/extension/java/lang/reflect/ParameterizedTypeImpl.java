package org.zero.common.core.extension.java.lang.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * copy from sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/27
 */
public class ParameterizedTypeImpl implements ParameterizedType {
	private final Type rawType;
	private final Type[] actualTypeArguments;
	private final Type ownerType;

	protected ParameterizedTypeImpl(Type rawType,
	                                Type[] actualTypeArguments,
	                                Type ownerType) {
		this.rawType = rawType;
		this.actualTypeArguments = actualTypeArguments == null ? null : actualTypeArguments.clone();
		if (rawType instanceof Class) {
			Class<?> rawClass = (Class<?>) rawType;
			ownerType = ownerType != null ? ownerType : rawClass.getEnclosingClass();
			validateConstructorArguments(rawClass);
		}
		this.ownerType = ownerType;
	}

	public static ParameterizedTypeImpl make(Type rawType, Type... actualTypeArguments) {
		return new ParameterizedTypeImpl(rawType, actualTypeArguments, null);
	}

	public static ParameterizedTypeImpl make(Type rawType, Type[] actualTypeArguments, Type ownerType) {
		return new ParameterizedTypeImpl(rawType, actualTypeArguments, ownerType);
	}

	@Override
	public Type[] getActualTypeArguments() {
		return actualTypeArguments == null ? null : actualTypeArguments.clone();
	}

	@Override
	public Type getRawType() {
		return rawType;
	}

	@Override
	public Type getOwnerType() {
		return ownerType;
	}

	protected void validateConstructorArguments(Class<?> rawClass) {
		TypeVariable<?>[] formals = rawClass.getTypeParameters();
		// check correct arity of actual type args
		if (formals.length != actualTypeArguments.length) {
			throw new IllegalArgumentException(String.format("Mismatch of count of " +
							"formal and actual type " +
							"arguments in constructor " +
							"of %s: %d formal argument(s) " +
							"%d actual argument(s)",
					rawClass.getName(),
					formals.length,
					actualTypeArguments.length));
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ParameterizedType) {
			// Check that information is equivalent
			ParameterizedType that = (ParameterizedType) o;

			if (this == that) {
				return true;
			}

			Type thatOwner = that.getOwnerType();
			Type thatRawType = that.getRawType();

			return Objects.equals(ownerType, thatOwner) &&
					Objects.equals(rawType, thatRawType) &&
					Arrays.equals(actualTypeArguments, // avoid clone
							that.getActualTypeArguments());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(actualTypeArguments) ^
				Objects.hashCode(ownerType) ^
				Objects.hashCode(rawType);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (ownerType != null) {
			if (ownerType instanceof Class) {
				sb.append(((Class<?>) ownerType).getName());
			} else {
				sb.append(ownerType);
			}

			sb.append("$");

			if (ownerType instanceof ParameterizedTypeImpl &&
					((ParameterizedTypeImpl) ownerType).rawType instanceof Class &&
					rawType instanceof Class) {
				// Find simple name of nested type by removing the shared prefix with owner.
				sb.append(((Class<?>) rawType).getName()
						.replace(((Class<?>) ((ParameterizedTypeImpl) ownerType).rawType).getName() + "$", ""));
			} else if (rawType instanceof Class) {
				sb.append(((Class<?>) rawType).getSimpleName());
			} else {
				sb.append(rawType.getTypeName());
			}
		} else {
			sb.append(rawType.getTypeName());
		}

		if (actualTypeArguments != null) {
			StringJoiner sj = new StringJoiner(", ", "<", ">");
			sj.setEmptyValue("");
			for (Type t : actualTypeArguments) {
				sj.add(t.getTypeName());
			}
			sb.append(sj);
		}

		return sb.toString();
	}
}
