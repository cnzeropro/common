package org.zero.common.core.support.api.crypto.encryption;

import org.zero.common.core.support.api.crypto.exception.CryptoException;
import org.zero.common.data.exception.Status;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/5
 */
public class EncryptionException extends CryptoException {
	public static final Status STATUS = Status.Default.of(Status.ERROR_CODE, "encryption exception");

	public EncryptionException() {
		super(STATUS);
	}

	public EncryptionException(String message) {
		super(message, STATUS);
	}

	public EncryptionException(Throwable cause) {
		super(cause, STATUS);
	}

	public EncryptionException(String message, Throwable cause) {
		super(message, cause, STATUS);
	}
}
