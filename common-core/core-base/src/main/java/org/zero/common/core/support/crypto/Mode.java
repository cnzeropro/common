package org.zero.common.core.support.crypto;

/**
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Cipher">JDK8 - Cipher (Encryption) Algorithms</a>
 * @see <a href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#cipher-algorithm-modes">JDK25 - Cipher Algorithm Modes</a>
 * @since 2025/10/20
 */

public enum Mode {
	/**
	 * 无模式
	 */
	NONE,
	/**
	 * 密码分组连接模式（Cipher Block Chaining）
	 */
	CBC,
	/**
	 * 密文反馈模式（Cipher Feedback）
	 */
	CFB,
	/**
	 * 计数器模式（Counter）
	 */
	CTR,
	/**
	 * Cipher Text Stealing
	 */
	CTS,
	/**
	 * 电子密码本模式（Electronic CodeBook）
	 */
	ECB,
	/**
	 * 输出反馈模式（Output Feedback）
	 */
	OFB,
	/**
	 * Galois/Counter Mode
	 */
	GCM,
	/**
	 * Counter with CBC-MAC
	 */
	CCM,
	/**
	 * XEX-based Tweaked CodeBook with CipherText Stealing
	 */
	XTS,
	/**
	 * Offset Codebook Mode
	 */
	OCB,
	/**
	 * Propagating Cipher Block Chaining
	 */
	PCBC,
	;
}
