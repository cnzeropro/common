package org.zero.common.core.extension.java.security;

import lombok.SneakyThrows;
import org.zero.common.core.extension.java.lang.BaseBuilder;
import org.zero.common.core.util.java.io.IoUtil;
import org.zero.common.core.util.java.lang.CharSequenceUtil;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.Provider;
import java.util.Objects;

/**
 * {@link KeyStore} 构建器
 * <p>
 * {@link KeyStore}：用于管理密钥和证书的存储库，可以用来存储密钥对、证书链、信任根证书等。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/30
 */
public class KeyStoreBuilder extends BaseBuilder<KeyStore, KeyStoreBuilder> {
    protected String type = KeyStore.getDefaultType();
    protected String providerName;
    protected Provider provider;
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
        this.providerName = providerName;
        return this;
    }

    public KeyStoreBuilder provider(Provider provider) {
        this.provider = provider;
        return this;
    }

    @SneakyThrows
    public KeyStoreBuilder filepath(String filepath) {
        return this.inputStream(Files.newInputStream(Paths.get(filepath)));
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
    protected KeyStore instance() {
        KeyStore keyStore;
        if (Objects.nonNull(provider)) {
            keyStore = KeyStore.getInstance(type, provider);
        } else if (CharSequenceUtil.nonBlank(providerName)) {
            keyStore = KeyStore.getInstance(type, providerName);
        } else {
            keyStore = KeyStore.getInstance(type);
        }
        keyStore.load(inputStream, password);
        IoUtil.close(inputStream);
        return keyStore;
    }
}
