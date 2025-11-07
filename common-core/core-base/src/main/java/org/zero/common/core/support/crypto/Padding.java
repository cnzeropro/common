package org.zero.common.core.support.crypto;

/**
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Cipher">JDK8 - Cipher (Encryption) Algorithms</a>
 * @see <a href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#cipher-algorithm-paddings">JDK25 - Cipher Algorithm Paddings</a>
 * @since 2025/10/20
 */
public enum Padding {
	/**
	 * 无补码
	 */
	NoPadding,
	/**
	 * 0 补码，即不满 block 长度时使用 0 填充
	 */
	ZeroPadding,
	/**
	 * The padding scheme described in PKCS #1
	 */
	PKCS1Padding,
	/**
	 * The padding scheme described in PKCS #5
	 */
	PKCS5Padding,
	/**
	 * The padding scheme described in PKCS #7
	 */
	PKCS7Padding,
	/**
	 * The padding scheme defined in the SSL Protocol Version 3.0
	 */
	SSL3Padding,
	/**
	 * This padding for block ciphers is described in the ISO 10126 standard (now withdrawn)
	 */
	ISO10126Padding,
	/**
	 * Optimal Asymmetric Encryption. <br>
	 * Padding scheme defined in PKCS #1
	 */
	OAEPPadding,
	;
}
