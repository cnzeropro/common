package org.zero.common.core.util.javax.crypto;


import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.data.constant.CharPool;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/17
 */
public class KeyUtil {

	/**
	 * 获取主体算法名，例如 RSA/ECB/PKCS1Padding 的主体算法是 RSA
	 */
	public static String getMainAlgorithm(String algorithm) {
		final int slashIndex = algorithm.indexOf(CharPool.SLASH);
		if (slashIndex > 0) {
			return algorithm.substring(0, slashIndex);
		}
		return algorithm;
	}

	/**
	 * 获取用于密钥生成的算法
	 * <p>
	 * 获取 XXXwithXXX 算法的后半部分算法，如果为 ECDSA 或 SM2，返回算法为 EC
	 */
	public static String getAlgorithmAfterWith(String algorithm) {
		if (StringUtil.startWith(algorithm, "ECIESWith", true)) {
			return "EC";
		}

		algorithm = getMainAlgorithm(algorithm);
		int indexOfWith = StringUtil.lastIndexOf(algorithm, "with", true);
		if (indexOfWith > 0) {
			algorithm = StringUtil.sub(algorithm, indexOfWith + "with".length());
		}
		if ("ECDSA".equalsIgnoreCase(algorithm)
			|| "SM2".equalsIgnoreCase(algorithm)
			|| "ECIES".equalsIgnoreCase(algorithm)) {
			algorithm = "EC";
		}
		return algorithm;
	}
}
