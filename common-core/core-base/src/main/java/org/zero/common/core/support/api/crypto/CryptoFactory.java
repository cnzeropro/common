package org.zero.common.core.support.api.crypto;

import cn.hutool.core.map.WeakConcurrentMap;
import lombok.experimental.UtilityClass;
import org.zero.common.core.support.api.crypto.strategy.AesCrypto;
import org.zero.common.core.support.api.crypto.strategy.RsaCrypto;
import org.zero.common.core.support.cache.Cache;
import org.zero.common.core.support.cache.MapCache;
import org.zero.common.core.support.crypto.Crypto;
import org.zero.common.core.util.java.lang.reflect.MemberUtil;
import org.zero.common.core.util.javax.crypto.KeyUtil;
import org.zero.common.data.exception.UtilException;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/30
 */
@UtilityClass
public class CryptoFactory {
	private static final Cache<String, Class<? extends Crypto>> CRYPTO_CLASS_CACHE = MapCache.of(ConcurrentHashMap::new);
	private static final Cache<CryptoContext, Crypto> CRYPTO_CACHE = MapCache.of(WeakConcurrentMap::new);

	static {
		put("AES", AesCrypto.class);
		put("RSA", RsaCrypto.class);
	}

	public static void put(String algorithm, Class<? extends Crypto> cryptoClass) {
		CRYPTO_CLASS_CACHE.put(algorithm, cryptoClass);
	}

	public static Crypto get(CryptoContext context) {
		String algorithm = context.getAlgorithm();
		Class<? extends Crypto> cryptoClass = CRYPTO_CLASS_CACHE.getOrSupply(algorithm,
			() -> {
				String mainAlgorithm = KeyUtil.getMainAlgorithm(algorithm);
				return CRYPTO_CLASS_CACHE.get(mainAlgorithm);
			});
		if (Objects.isNull(cryptoClass)) {
			throw new UtilException("Crypto class not found for algorithm: " + algorithm);
		}
		return CRYPTO_CACHE.computeIfAbsent(context, c -> {
			Crypto crypto = MemberUtil.getInstance(cryptoClass, true, new Object[]{c});
			if (Objects.isNull(crypto)) {
				crypto = MemberUtil.getInstance(cryptoClass, true);
			}
			if (Objects.isNull(crypto)) {
				throw new UtilException("Crypto not found for algorithm: " + algorithm);
			}
			return crypto;
		});
	}
}
