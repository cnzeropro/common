package org.zero.common.core.support.codec;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CipherMode;
import cn.hutool.crypto.KeyUtil;
import org.zero.common.core.extension.java.util.ReferenceLimitedMap;
import org.zero.common.core.extension.java.util.ReferenceType;
import org.zero.common.core.support.cache.Cache;
import org.zero.common.core.support.cache.MapCache;
import org.zero.common.core.support.codec.decryption.Decryption;
import org.zero.common.core.support.codec.encryption.Encryption;
import org.zero.common.core.support.codec.strategy.CodecStrategy;
import org.zero.common.core.support.codec.strategy.CodecStrategyFactory;
import org.zero.common.core.support.codec.strategy.StrategyContext;
import org.zero.common.core.support.codec.supplier.KeyContext;
import org.zero.common.core.support.codec.supplier.KeySupplier;
import org.zero.common.core.support.codec.supplier.NonKeySupplier;
import org.zero.common.core.util.java.lang.reflect.ExecutableUtil;
import org.zero.common.core.util.java.lang.reflect.MemberUtil;
import org.zero.common.data.exception.CommonException;

import java.lang.reflect.Executable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/24
 */
public class CodecUtil {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String DEFAULT_ALGORITHM_SEPARATOR = "/";

    protected static final Cache<String, CodecStrategy> CACHE = MapCache.of(()-> ReferenceLimitedMap.<String, CodecStrategy>builder()
            .maxCapacity(10000)
            .accessOrder(true)
            .lazyCleanup(false)
            .referenceType(ReferenceType.WEAK)
            .build());

    public static byte[] encrypt(byte[] data, Executable executable, Encryption encryption, CodecProperties.CodecConfiguration config) {
        CodecContext codecContext = toCodecContext(encryption, config);
        CodecStrategy codecStrategy = getCodecStrategy(executable, codecContext);
        return codecStrategy.encrypt(data);
    }

    public static byte[] encrypt(String data, Executable executable, Encryption encryption, CodecProperties.CodecConfiguration config) {
        CodecContext codecContext = toCodecContext(encryption, config);
        CodecStrategy codecStrategy = getCodecStrategy(executable, codecContext);
        byte[] byteArray = data.getBytes(DEFAULT_CHARSET);
        return codecStrategy.encrypt(byteArray);
    }

    public static String encryptToStr(byte[] data, Executable executable, Encryption encryption, CodecProperties.CodecConfiguration config) {
        CodecContext codecContext = toCodecContext(encryption, config);
        CodecStrategy codecStrategy = getCodecStrategy(executable, codecContext);
        byte[] encrypted = codecStrategy.encrypt(data);
        return codecContext.getStringMode().toString(encrypted);
    }

    public static String encryptToStr(String data, Executable executable, Encryption encryption, CodecProperties.CodecConfiguration config) {
        CodecContext codecContext = toCodecContext(encryption, config);
        CodecStrategy codecStrategy = getCodecStrategy(executable, codecContext);
        byte[] byteArray = data.getBytes(DEFAULT_CHARSET);
        byte[] encrypted = codecStrategy.encrypt(byteArray);
        return codecContext.getStringMode().toString(encrypted);
    }


    public static byte[] decrypt(byte[] data, Executable executable, Decryption decryption, CodecProperties.CodecConfiguration config) {
        CodecContext codecContext = toCodecContext(decryption, config);
        CodecStrategy codecStrategy = getCodecStrategy(executable, codecContext);
        return codecStrategy.decrypt(data);
    }

    public static byte[] decrypt(String data, Executable executable, Decryption decryption, CodecProperties.CodecConfiguration config) {
        CodecContext codecContext = toCodecContext(decryption, config);
        CodecStrategy codecStrategy = getCodecStrategy(executable, codecContext);
        byte[] byteArray = codecContext.getStringMode().toBytes(data);
        return codecStrategy.decrypt(byteArray);
    }

    public static String decryptToStr(byte[] data, Executable executable, Decryption decryption, CodecProperties.CodecConfiguration config) {
        CodecContext codecContext = toCodecContext(decryption, config);
        CodecStrategy codecStrategy = getCodecStrategy(executable, codecContext);
        byte[] decrypted = codecStrategy.decrypt(data);
        return codecContext.getStringMode().toString(decrypted);
    }

    public static String decryptToStr(String data, Executable executable, Decryption decryption, CodecProperties.CodecConfiguration config) {
        CodecContext codecContext = toCodecContext(decryption, config);
        CodecStrategy codecStrategy = getCodecStrategy(executable, codecContext);
        byte[] byteArray = codecContext.getStringMode().toBytes(data);
        byte[] decrypted = codecStrategy.decrypt(byteArray);
        return new String(decrypted, DEFAULT_CHARSET);
    }

    protected static CodecStrategy getCodecStrategy(Executable executable, CodecContext codecContext) {
        String name = String.format("%s|%s", ExecutableUtil.FQNBuilder.of(executable).build(), codecContext.getCipherMode());
        return CACHE.mapAndPutIfAbsent(name, n -> createCodecStrategy(codecContext));
    }

    protected static CodecStrategy createCodecStrategy(CodecContext codecContext) {
        String algorithm = codecContext.getAlgorithm();
        byte[] key = Opt.ofNullable(codecContext.getKey())
                .filter(ArrayUtil::isNotEmpty)
                .or(() -> {
                    KeyContext keyContext = KeyContext.builder()
                            .algorithm(algorithm)
                            .build();
                    return Opt.ofNullable(codecContext.getKeySupplier())
                            .map(keyProvider -> keyProvider.generate(keyContext))
                            .filter(ArrayUtil::isNotEmpty);
                })
                .orElseThrow(CommonException::new, "The key is empty");
        StrategyContext strategyContext = StrategyContext.builder()
                .algorithm(algorithm)
                .key(key)
                .cipherMode(codecContext.getCipherMode())
                .properties(codecContext.getProperties())
                .build();
        String mainAlgorithm = KeyUtil.getMainAlgorithm(algorithm);
        return CodecStrategyFactory.get(mainAlgorithm, strategyContext);
    }

    protected static CodecContext toCodecContext(Encryption encryption, CodecProperties.CodecConfiguration config) {
        Opt<Encryption> encryptionOpt = Opt.ofNullable(encryption);
        Opt<CodecProperties.CodecConfiguration> configOpt = Opt.ofNullable(config);
        String algorithm = encryptionOpt.map(Encryption::algorithm)
                .filter(StrUtil::isNotEmpty)
                .or(() -> configOpt.map(CodecProperties.CodecConfiguration::getAlgorithm)
                        .filter(StrUtil::isNotEmpty))
                .orElseThrow(NoSuchElementException::new, "No algorithm present");
        byte[] key = encryptionOpt.map(Encryption::key)
                .filter(StrUtil::isNotEmpty)
                .or(() -> configOpt.map(CodecProperties.CodecConfiguration::getKey))
                .map(CodecUtil::decode)
                .orElseGet(() -> new byte[0]);
        KeySupplier keyProvider = encryptionOpt.<Class<? extends KeySupplier>>map(Encryption::keyProvider)
                .or(() -> configOpt.map(CodecProperties.CodecConfiguration::getKeySupplier))
                .<KeySupplier>map(MemberUtil::getInstance)
                .orElse(NonKeySupplier.INSTANCE);
        StringMode stringMode = encryptionOpt.map(Encryption::stringMode)
                .or(() -> Opt.ofNullable(config.getStringMode()))
                .orElse(StringMode.HEX);
        return CodecContext.builder()
                .algorithm(algorithm)
                .key(key)
                .keySupplier(keyProvider)
                .stringMode(stringMode)
                .cipherMode(CipherMode.encrypt)
                .properties(config.getProperties())
                .build();
    }

    protected static CodecContext toCodecContext(Decryption decryption, CodecProperties.CodecConfiguration config) {
        Opt<Decryption> decryptionOpt = Opt.ofNullable(decryption);
        Opt<CodecProperties.CodecConfiguration> configOpt = Opt.ofNullable(config);
        String algorithm = decryptionOpt.map(Decryption::algorithm)
                .filter(StrUtil::isNotEmpty)
                .or(() -> configOpt.map(CodecProperties.CodecConfiguration::getAlgorithm)
                        .filter(StrUtil::isNotEmpty))
                .orElseThrow(NoSuchElementException::new, "No algorithm present");
        byte[] key = decryptionOpt.map(Decryption::key)
                .filter(StrUtil::isNotEmpty)
                .or(() -> configOpt.map(CodecProperties.CodecConfiguration::getKey))
                .map(CodecUtil::decode)
                .orElseGet(() -> new byte[0]);
        KeySupplier keyProvider = decryptionOpt.<Class<? extends KeySupplier>>map(Decryption::keyProvider)
                .or(() -> configOpt.map(CodecProperties.CodecConfiguration::getKeySupplier))
                .<KeySupplier>map(MemberUtil::getInstance)
                .orElse(NonKeySupplier.INSTANCE);
        StringMode stringMode = decryptionOpt.map(Decryption::stringMode)
                .or(() -> Opt.ofNullable(config.getStringMode()))
                .orElse(StringMode.HEX);
        return CodecContext.builder()
                .algorithm(algorithm)
                .key(key)
                .keySupplier(keyProvider)
                .stringMode(stringMode)
                .cipherMode(CipherMode.decrypt)
                .properties(config.getProperties())
                .build();
    }

    public static byte[] createKey(int numBit) {
        return RandomUtil.randomBytes(numBit / 8);
    }

    public static String createHexKey(int numBit, boolean lowerCase) {
        byte[] bytes = createKey(numBit);
        return HexUtil.encodeHexStr(bytes, lowerCase);
    }

    public static String createBase64Key(int numBit) {
        byte[] bytes = createKey(numBit);
        return Base64.encode(bytes);
    }

    public static String base64KeyToHex(String str) {
        byte[] bytes = Base64.decode(str);
        return HexUtil.encodeHexStr(bytes);
    }

    public static String hexKeyToBase64(String str) {
        byte[] bytes = HexUtil.decodeHex(str);
        return Base64.encode(bytes);
    }

    public static KeyPair createKeyPair(String algorithm, int keySize) {
        return KeyUtil.generateKeyPair(algorithm, keySize);
    }

    public static byte[] decode(String str) {
        if (StrUtil.isEmpty(str)) {
            return new byte[0];
        }
        if (Base64.isBase64(str)) {
            return Base64.decode(str);
        }
        if (HexUtil.isHexNumber(str)) {
            return HexUtil.decodeHex(str);
        }
        return CharSequenceUtil.bytes(str, DEFAULT_CHARSET);
    }

    public static CodecProperties.CodecConfiguration getConfig(Executable executable, Map<String, CodecProperties.CodecConfiguration> configMap) {
        // 类的全限定名
        String className = executable.getDeclaringClass().getName();
        // 方法名
        String executableName = executable.getName();
        // 参数类型列表（全限定名）
        String params = Arrays.stream(executable.getParameterTypes())
                .map(Class::getName)
                .collect(Collectors.joining(", "));
        // 方法全限定名
        String fullName = String.format("%s.%s(%s)", className, executableName, params);
        CodecProperties.CodecConfiguration config = configMap.get(fullName);
        if (Objects.isNull(config)) {
            String key = String.format("%s.%s", className, executableName);
            config = configMap.get(key);
        }
        if (Objects.isNull(config)) {
            config = configMap.get(className);
        }
        if (Objects.isNull(config)) {
            String key = String.format("%s(%s)", executableName, params);
            config = configMap.get(key);
        }
        if (Objects.isNull(config)) {
            config = configMap.get(executableName);
        }
        return config;
    }

    protected CodecUtil() {
        throw new UnsupportedOperationException();
    }
}
