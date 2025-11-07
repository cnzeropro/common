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
public enum KeyType {
	PUBLIC_KEY(Cipher.PUBLIC_KEY),
	PRIVATE_KEY(Cipher.PRIVATE_KEY),
	SECRET_KEY(Cipher.SECRET_KEY);

	private final int type;
}
