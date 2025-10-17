package org.zero.common.core.extension.javax.net.ssl;

import lombok.SneakyThrows;
import org.zero.common.core.extension.java.lang.Builder;
import org.zero.common.core.util.java.lang.CharSequenceUtil;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import java.security.KeyStore;
import java.security.Provider;
import java.util.Objects;

/**
 * {@link KeyManager} 构建器
 * <p>
 * {@link KeyManager}：用于 HTTPS 双向认证时，客户端向服务端发送的认证信息（证书）。<br>
 * 注意：与不同的服务端交互时，客户端可能用到不同的身份（证书）。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/30
 */
public class KeyManagerBuilder implements Builder<KeyManager[], KeyManagerBuilder> {
    protected String algorithm = KeyManagerFactory.getDefaultAlgorithm();
    protected String providerName;
    protected Provider provider;
    protected KeyStore keyStore;
    protected char[] password;

    protected KeyManagerBuilder() {
    }

    public static KeyManagerBuilder create() {
        return new KeyManagerBuilder();
    }

    public KeyManagerBuilder algorithm(String algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public KeyManagerBuilder providerName(String providerName) {
        this.providerName = providerName;
        return this;
    }

    public KeyManagerBuilder provider(Provider provider) {
        this.provider = provider;
        return this;
    }

    public KeyManagerBuilder keyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
        return this;
    }

    public KeyManagerBuilder password(CharSequence password) {
        int length = password.length();
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = password.charAt(i);
        }
        return this.password(chars);
    }

    public KeyManagerBuilder password(char[] password) {
        this.password = password;
        return this;
    }

    @SneakyThrows
    @Override
    public KeyManager[] build() {
        KeyManagerFactory factory;
        if (Objects.nonNull(provider)) {
            factory = KeyManagerFactory.getInstance(algorithm, provider);
        } else if (CharSequenceUtil.nonBlank(providerName)) {
            factory = KeyManagerFactory.getInstance(algorithm, providerName);
        } else {
            factory = KeyManagerFactory.getInstance(algorithm);
        }
        factory.init(keyStore, password);
        return factory.getKeyManagers();
    }
}
