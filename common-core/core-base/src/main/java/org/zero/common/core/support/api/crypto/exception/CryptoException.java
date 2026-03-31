package org.zero.common.core.support.api.crypto.exception;

import org.zero.common.data.enumeration.Status;
import org.zero.common.data.exception.BaseException;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/5
 */
public class CryptoException extends BaseException {
	public static final Status STATUS = Status.Default.of(Status.ERROR_CODE, "Crypto exception");

	public CryptoException() {
		super(STATUS);
	}

	public CryptoException(Status status) {
		super(status);
	}

	public CryptoException(String message) {
		super(message, STATUS);
	}

	public CryptoException(String message, Status status) {
		super(message, status);
	}

	public CryptoException(Throwable cause) {
		super(cause, STATUS);
	}

	public CryptoException(Throwable cause, Status status) {
		super(cause, status);
	}

	public CryptoException(String message, Throwable cause) {
		super(message, cause, STATUS);
	}

	public CryptoException(String message, Throwable cause, Status status) {
		super(message, cause, status);
	}
}
