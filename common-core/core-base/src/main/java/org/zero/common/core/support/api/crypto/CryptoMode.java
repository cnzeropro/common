package org.zero.common.core.support.api.crypto;

import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/31
 */
@RequiredArgsConstructor
public enum CryptoMode {
	/**
	 * 加密模式
	 */
	ENCRYPT(Cipher.ENCRYPT_MODE),
	/**
	 * 解密模式
	 */
	DECRYPT(Cipher.DECRYPT_MODE),
	;

	private final int value;
}
