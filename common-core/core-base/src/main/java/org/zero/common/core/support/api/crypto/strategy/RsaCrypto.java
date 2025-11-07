package org.zero.common.core.support.api.crypto.strategy;

import lombok.experimental.Delegate;
import org.zero.common.core.extension.java.util.MapHelper;
import org.zero.common.core.support.api.crypto.CryptoContext;
import org.zero.common.core.support.api.crypto.CryptoMode;
import org.zero.common.core.support.api.crypto.exception.CryptoException;
import org.zero.common.core.support.crypto.Crypto;
import org.zero.common.core.support.crypto.asymmetric.RSA;

import javax.crypto.spec.OAEPParameterSpec;
import java.security.Provider;
import java.security.Security;
import java.security.spec.PSSParameterSpec;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/3
 */
public class RsaCrypto implements Crypto {
	@Delegate
	protected final RSA rsa;

	public RsaCrypto(CryptoContext context) {
		MapHelper config = context.getConfig();
		Provider provider = config.get("provider", Provider.class);
		if (Objects.isNull(provider)) {
			String providerName = config.getString("providerName");
			provider = Security.getProvider(providerName);
		}

		CryptoMode cryptoMode = context.getCryptoMode();
		if (cryptoMode == CryptoMode.ENCRYPT) {
			rsa = new RSA(context.getAlgorithm(), provider, context.getKey(), null);
		} else if (cryptoMode == CryptoMode.DECRYPT) {
			rsa = new RSA(context.getAlgorithm(), provider, null, context.getKey());
		} else {
			// never
			throw new CryptoException("CryptoMode is not supported: " + cryptoMode);
		}

		// OAEP 填充模式下设置 AlgorithmParameterSpec
		OAEPParameterSpec oaepParameterSpec = config.get("OAEPParameterSpec", OAEPParameterSpec.class);
		if (Objects.nonNull(oaepParameterSpec)) {
			rsa.setAlgorithmParameterSpec(oaepParameterSpec);
		}
		// PSS 签名模式下设置 AlgorithmParameterSpec
		PSSParameterSpec pssParameterSpec = config.get("PSSParameterSpec", PSSParameterSpec.class);
		if (Objects.nonNull(pssParameterSpec)) {
			rsa.setAlgorithmParameterSpec(pssParameterSpec);
		}
	}
}
