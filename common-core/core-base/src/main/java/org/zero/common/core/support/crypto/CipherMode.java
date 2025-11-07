package org.zero.common.core.support.crypto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/3
 */
@Getter
@RequiredArgsConstructor
public enum CipherMode {
	ENCRYPT(Cipher.ENCRYPT_MODE),
	DECRYPT(Cipher.DECRYPT_MODE),
	WRAP(Cipher.WRAP_MODE),
	UNWRAP(Cipher.UNWRAP_MODE);

	private final int mode;
}
