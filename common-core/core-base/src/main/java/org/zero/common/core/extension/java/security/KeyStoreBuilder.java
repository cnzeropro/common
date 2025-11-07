package org.zero.common.core.extension.java.security;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.zero.common.core.extension.java.lang.Builder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.util.Objects;

/**
 * {@link KeyStore} 构建器
 * <p>
 * {@link KeyStore}：用于管理密钥和证书的存储库，可以用来存储密钥对、证书链、信任根证书等。
 *
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#KeyStore">KeyStore Types</a>
 * @since 2025/4/30
 */
public class KeyStoreBuilder implements Builder<KeyStore, KeyStoreBuilder> {
	protected String type = KeyStore.getDefaultType();
	protected Provider provider;
	protected KeyStore.LoadStoreParameter loadStoreParameter;
	protected byte[] bytes;
	protected InputStream inputStream;
	protected char[] password;

	protected KeyStoreBuilder() {
	}

	public static KeyStoreBuilder create() {
		return new KeyStoreBuilder();
	}

	public KeyStoreBuilder type(String type) {
		this.type = type;
		return this;
	}

	public KeyStoreBuilder providerName(String providerName) {
		Provider provider = Security.getProvider(providerName);
		return this.provider(provider);
	}

	public KeyStoreBuilder provider(Provider provider) {
		this.provider = provider;
		return this;
	}

	public KeyStoreBuilder loadStoreParameter(KeyStore.LoadStoreParameter loadStoreParameter) {
		this.loadStoreParameter = loadStoreParameter;
		return this;
	}

	public KeyStoreBuilder bytes(byte[] bytes) {
		this.bytes = bytes;
		return this;
	}

	public KeyStoreBuilder filepath(String filepath) {
		return this.path(Paths.get(filepath));
	}

	@SneakyThrows
	public KeyStoreBuilder path(Path path) {
		return this.inputStream(Files.newInputStream(path));
	}

	public KeyStoreBuilder inputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		return this;
	}

	public KeyStoreBuilder password(CharSequence password) {
		int length = password.length();
		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			chars[i] = password.charAt(i);
		}
		return this.password(chars);
	}

	public KeyStoreBuilder password(char[] password) {
		this.password = password;
		return this;
	}

	@SneakyThrows
	@Override
	public KeyStore build() {
		KeyStore keyStore = Objects.nonNull(provider) ? KeyStore.getInstance(type, provider) : KeyStore.getInstance(type);
		if (Objects.nonNull(loadStoreParameter)) {
			keyStore.load(loadStoreParameter);
		} else {
			@Cleanup InputStream inputStream = Objects.nonNull(bytes) ? new ByteArrayInputStream(bytes) : this.inputStream;
			Objects.requireNonNull(inputStream, "inputStream cannot be null");
			Objects.requireNonNull(password, "password cannot be null");
			keyStore.load(inputStream, password);
		}
		return keyStore;
	}
}
