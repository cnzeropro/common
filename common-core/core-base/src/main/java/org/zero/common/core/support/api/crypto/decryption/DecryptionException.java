package org.zero.common.core.support.api.crypto.decryption;

import org.zero.common.core.support.api.crypto.exception.CryptoException;
import org.zero.common.data.exception.Status;

/**
 * 解密异常
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/5
 */
public class DecryptionException extends CryptoException {
	public static final Status STATUS = Status.Default.of(Status.ERROR_CODE, "decryption exception");

	public DecryptionException() {
		super(STATUS);
	}

	public DecryptionException(String message) {
		super(message, STATUS);
	}

	public DecryptionException(Throwable cause) {
		super(cause, STATUS);
	}

	public DecryptionException(String message, Throwable cause) {
		super(message, cause, STATUS);
	}
}
