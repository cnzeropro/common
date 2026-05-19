package org.zero.common.core.extension.jakarta.validation;

import jakarta.validation.Payload;

/**
 * Bean Validation 约束严重级别。
 * <p>
 * 该类仅提供 {@link Payload} marker，可通过
 * {@code ConstraintViolation#getConstraintDescriptor().getPayload()} 读取。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
public final class ValidationSeverity {
	private ValidationSeverity() {
	}

	/**
	 * 信息级约束。
	 */
	public static final class Info implements Payload {
		private Info() {
		}
	}

	/**
	 * 警告级约束。
	 */
	public static final class Warning implements Payload {
		private Warning() {
		}
	}

	/**
	 * 错误级约束。
	 */
	public static final class Error implements Payload {
		private Error() {
		}
	}
}
