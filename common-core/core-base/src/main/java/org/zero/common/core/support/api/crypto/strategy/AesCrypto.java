package org.zero.common.core.support.api.crypto.strategy;

import lombok.experimental.Delegate;
import org.zero.common.core.extension.java.util.MapHelper;
import org.zero.common.core.support.api.crypto.CryptoContext;
import org.zero.common.core.support.api.crypto.StringMode;
import org.zero.common.core.support.crypto.Crypto;
import org.zero.common.core.support.crypto.symmetric.AES;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.ClassLoaderUtil;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.core.util.java.lang.reflect.MemberUtil;

import java.security.Provider;
import java.security.Security;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/3
 */
public class AesCrypto implements Crypto {
	@Delegate
	protected final AES aes;

	public AesCrypto(CryptoContext context) {
		MapHelper config = context.getConfig();
		Provider provider = config.get("provider", Provider.class);
		if (Objects.isNull(provider)) {
			String providerName = config.getString("providerName");
			provider = Security.getProvider(providerName);
		}

		aes = new AES(context.getAlgorithm(), provider, context.getKey());

		// 设置 iv
		byte[] iv = config.get("iv", byte[].class);
		if (Objects.isNull(iv)) {
			String ivString = config.getString("ivString");
			if (CharSequenceUtil.nonEmpty(ivString)) {
				StringMode ivStringMode = config.get("ivStringMode", StringMode.class);
				iv = Objects.isNull(ivStringMode) ? null : ivStringMode.toBytes(ivString);
				if (Objects.isNull(iv)) {
					iv = Optional.ofNullable(config.getString("ivStringModeClassName"))
						.flatMap(ClassLoaderUtil::loadClassOpt)
						.filter(clazz -> ClassUtil.isAssignable(StringMode.class, clazz))
						.map(clazz -> (Class<StringMode>) clazz)
						.map(MemberUtil::getInstance)
						.map(stringMode -> stringMode.toBytes(ivString))
						.orElse(null);

				}
			}
		}
		aes.setIv(iv);
	}
}
