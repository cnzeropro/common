package org.zero.common.core.support.api.crypto;

import lombok.experimental.UtilityClass;
import org.zero.common.core.extension.java.util.MapHelper;
import org.zero.common.core.support.api.crypto.decryption.Decryption;
import org.zero.common.core.support.api.crypto.encryption.Encryption;
import org.zero.common.core.support.api.crypto.supplier.CryptoConfigSupplier;
import org.zero.common.core.support.api.crypto.supplier.KeyContext;
import org.zero.common.core.support.api.crypto.supplier.KeySupplier;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.ObjectUtil;
import org.zero.common.core.util.java.lang.reflect.MemberUtil;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;

/**
 * 接口加解密工具类（内部使用）
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/31
 */
@UtilityClass
public class CryptoUtil {

	public static CryptoContext getContext(Encryption encryption, CryptoProperties.EncryptionProperties encryptionProperties) {
		String algorithm = ObjectUtil.defaultIfEmpty(encryption.algorithm(), encryptionProperties.getAlgorithm());
		Class<? extends CryptoConfigSupplier> configSupplierClass = encryption.configSupplier();
		Map<String, Object> map = encryptionProperties.getProperties();
		if (canInstanced(configSupplierClass)) {
			CryptoConfigSupplier configSupplier = MemberUtil.getInstance(configSupplierClass);
			map.putAll(configSupplier.getConfig());
		}
		MapHelper config = new MapHelper(map);
		byte[] key;
		String keyString = ObjectUtil.defaultIfEmpty(encryption.keyString(), encryptionProperties.getKeyString());
		if (CharSequenceUtil.nonEmpty(keyString)) {
			StringMode keyStringMode = getInstance(encryption.keyStringMode(), encryptionProperties.getKeyStringMode());
			key = keyStringMode.toBytes(keyString);
		} else {
			KeySupplier keySupplier = getInstance(encryption.keyProvider(), encryptionProperties.getKeyProvider());
			KeyContext keyContext = KeyContext.builder()
				.algorithm(algorithm)
				.config(config)
				.build();
			key = keySupplier.generate(keyContext);
		}
		StringMode sourceStringMode = getInstance(encryption.sourceStringMode(), encryptionProperties.getSourceStringMode());
		StringMode targetStringMode = getInstance(encryption.targetStringMode(), encryptionProperties.getTargetStringMode());
		return CryptoContext.builder()
			.algorithm(algorithm)
			.key(key)
			.cryptoMode(CryptoMode.DECRYPT)
			.config(config)
			.sourceStringMode(sourceStringMode)
			.targetStringMode(targetStringMode)
			.build();
	}

	public static CryptoContext getContext(Decryption decryption, CryptoProperties.DecryptionProperties decryptionProperties) {
		String algorithm = ObjectUtil.defaultIfEmpty(decryption.algorithm(), decryptionProperties.getAlgorithm());
		Class<? extends CryptoConfigSupplier> configSupplierClass = decryption.configSupplier();
		Map<String, Object> map = decryptionProperties.getProperties();
		if (canInstanced(configSupplierClass)) {
			CryptoConfigSupplier configSupplier = MemberUtil.getInstance(configSupplierClass);
			map.putAll(configSupplier.getConfig());
		}
		MapHelper config = new MapHelper(map);
		byte[] key;
		String keyString = ObjectUtil.defaultIfEmpty(decryption.keyString(), decryptionProperties.getKeyString());
		if (CharSequenceUtil.nonEmpty(keyString)) {
			StringMode keyStringMode = getInstance(decryption.keyStringMode(), decryptionProperties.getKeyStringMode());
			key = keyStringMode.toBytes(keyString);
		} else {
			KeySupplier keySupplier = getInstance(decryption.keyProvider(), decryptionProperties.getKeyProvider());
			KeyContext keyContext = KeyContext.builder()
				.algorithm(algorithm)
				.config(config)
				.build();
			key = keySupplier.generate(keyContext);
		}
		StringMode sourceStringMode = getInstance(decryption.sourceStringMode(), decryptionProperties.getSourceStringMode());
		StringMode targetStringMode = getInstance(decryption.targetStringMode(), decryptionProperties.getTargetStringMode());
		return CryptoContext.builder()
			.algorithm(algorithm)
			.key(key)
			.cryptoMode(CryptoMode.DECRYPT)
			.config(config)
			.sourceStringMode(sourceStringMode)
			.targetStringMode(targetStringMode)
			.build();
	}


	static boolean canInstanced(Class<?> clazz) {
		return Objects.nonNull(clazz) &&
			!clazz.isInterface() &&
			!Modifier.isAbstract(clazz.getModifiers());
	}

	public static <T> T getInstance(Class<? extends T> inAnnotation, Class<? extends T> inConfig, Object... args) {
		Class<? extends T> clazz = inAnnotation;
		if (!canInstanced(inAnnotation)) {
			clazz = inConfig;
		}
		return MemberUtil.getInstance(clazz, args);
	}
}
