package org.zero.common.core.util.java.codec;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/11/26
 */
@UtilityClass
public class KeyUtil {
    /**
     * Java密钥库（Java Key Store，JKS）
     */
    public static final String KEY_TYPE_JKS = "JKS";
    /**
     * jceks
     */
    public static final String KEY_TYPE_JCEKS = "jceks";
    /**
     * PKCS12是公钥加密标准，它规定了可包含所有私钥、公钥和证书。其以二进制格式存储，也称为PFX文件
     */
    public static final String KEY_TYPE_PKCS12 = "pkcs12";
    /**
     * Certification类型：X.509
     */
    public static final String CERT_TYPE_X509 = "X.509";
    /**
     * SM2默认曲线
     */
    public static final String SM2_DEFAULT_CURVE = "sm2p256v1";

    /**
     * 密钥字节长度
     */
    private final int KEY_BITS = 108;
    /**
     * 密钥长度
     */
    private final int KEY_SIZE = 1024;
    /**
     * 迭代次数
     */
    private final int ITERATION_COUNT = 1000;

    /* ********************************************** SecretKey ********************************************** */

    /**
     * 生成 {@link SecretKey}
     * 用于对称加密，摘要算法和PBE算法密钥生成
     *
     * @param algorithm
     * @param key
     * @return
     */
    @SneakyThrows
    public SecretKey generateKey(String algorithm, byte[] key) {
        if (Objects.isNull(key)) {
            return generateKey(algorithm);
        }

        String mainAlgorithm = getMainAlgorithm(algorithm).toUpperCase();
        if ("PBE".equals(mainAlgorithm) || "DES".equals(mainAlgorithm)) {
            // PBE和DES密钥
            return generateKey(algorithm, getKeySpec(algorithm, key));
        } else {
            // 其它算法密钥
            if (mainAlgorithm.startsWith("AES")) {
                key = Arrays.copyOf(key, 24);
            }
            // if ("AES".equals(mainAlgorithm)) {
            //     key = Arrays.copyOf(key, 16);
            // }
            return new SecretKeySpec(key, algorithm);
        }
    }

    /**
     * 生成 {@link SecretKey}
     * 用于对称加密，摘要算法和PBE算法密钥生成
     *
     * @param algorithm
     * @param keySpec
     * @return
     */
    @SneakyThrows
    public SecretKey generateKey(String algorithm, KeySpec keySpec) {
        return getSecretKeyFactory(algorithm).generateSecret(keySpec);
    }

    /**
     * 生成 {@link SecretKey}，无需指定原始密钥
     * 用于对称加密，摘要和PBE算法密钥生成
     * 当指定keySize<0时，AES默认长度为128，其它算法不指定
     *
     * @param algorithm
     * @return
     */
    public SecretKey generateKey(String algorithm) {
        return generateKey(algorithm, -1);
    }

    /**
     * 生成 {@link SecretKey}，无需指定原始密钥
     * 用于对称加密，摘要和PBE算法密钥生成
     * 当指定keySize<0时，AES默认长度为128，其它算法不指定
     *
     * @param algorithm
     * @param keySize
     * @return
     */
    public SecretKey generateKey(String algorithm, int keySize) {
        return generateKey(algorithm, keySize, null);
    }

    /**
     * 生成 {@link SecretKey}，无需指定原始密钥
     * 用于对称加密，摘要和PBE算法密钥生成
     * 当指定keySize<0时，AES默认长度为128，其它算法不指定
     *
     * @param algorithm
     * @param keySize
     * @param random
     * @return
     */
    public SecretKey generateKey(String algorithm, int keySize, SecureRandom random) {
        algorithm = getMainAlgorithm(algorithm);
        final KeyGenerator keyGenerator = getKeyGenerator(algorithm);
        if (keySize <= 0 && algorithm.startsWith(SymmetricAlgorithm.AES.getName())) {
            keySize = 128;
        }

        if (Objects.isNull(random)) {
            keyGenerator.init(keySize);
        } else {
            keyGenerator.init(keySize, random);
        }
        return keyGenerator.generateKey();
    }

    /* ********************************************** PublicKey ********************************************** */

    /**
     * 生成RSA算法的 {@link PublicKey}
     * 此算法用了默认补位方式为RSA/ECB/PKCS1Padding
     *
     * @param key 密钥，必须为DER编码存储
     * @return
     */
    public PublicKey generatePublicKey(byte[] key) {
        return generatePublicKey(AsymmetricAlgorithm.RSA.getName(), key);
    }

    /**
     * 生成 {@link PublicKey}
     * 仅用于非对称加密公钥生成
     *
     * @param algorithm
     * @param key       密钥，必须为DER编码存储
     * @return
     */
    public PublicKey generatePublicKey(String algorithm, byte[] key) {
        return generatePublicKey(algorithm, new X509EncodedKeySpec(key));
    }

    /**
     * 生成 {@link PublicKey}
     * 仅用于非对称加密公钥生成
     *
     * @param algorithm
     * @param keySpec
     * @return
     */
    @SneakyThrows
    public PublicKey generatePublicKey(String algorithm, KeySpec keySpec) {
        return getKeyFactory(getAlgorithmAfterWith(algorithm)).generatePublic(keySpec);
    }

    /**
     * 从 {@link KeyStore} 获取 {@link PublicKey}
     *
     * @param keyStore
     * @param alias
     * @return
     */
    @SneakyThrows
    public static PublicKey getPublicKey(KeyStore keyStore, String alias) {
        return keyStore.getCertificate(alias).getPublicKey();
    }
    /* ********************************************** PrivateKey ********************************************** */

    /**
     * 生成RSA算法的 {@link PrivateKey}
     * 此算法用了默认补位方式为RSA/ECB/PKCS1Padding
     *
     * @param key
     * @return
     */
    public PrivateKey generatePrivateKey(byte[] key) {
        return generatePrivateKey(AsymmetricAlgorithm.RSA.getName(), key);
    }

    /**
     * 生成 {@link PrivateKey}
     * 仅用于非对称加密私钥生成
     *
     * @param algorithm
     * @param key
     * @return
     */
    public PrivateKey generatePrivateKey(String algorithm, byte[] key) {
        return generatePrivateKey(algorithm, new PKCS8EncodedKeySpec(key));
    }

    /**
     * 生成 {@link PrivateKey}
     * 仅用于非对称加密私钥生成
     *
     * @param algorithm
     * @param keySpec
     * @return
     */
    @SneakyThrows
    public PrivateKey generatePrivateKey(String algorithm, KeySpec keySpec) {
        return getKeyFactory(getAlgorithmAfterWith(algorithm)).generatePrivate(keySpec);
    }

    /**
     * 从 {@link KeyStore} 获取 {@link PublicKey}
     *
     * @param keyStore
     * @param alias
     * @param password
     * @return
     */
    @SneakyThrows
    public PrivateKey getPrivateKey(KeyStore keyStore, String alias, char[] password) {
        return (PrivateKey) keyStore.getKey(alias, password);
    }

    /* ********************************************** KeyPair ********************************************** */

    /**
     * 生成 {@link KeyPair}
     * 仅用于非对称加密密钥对生成
     *
     * @param algorithm
     * @return
     */
    public KeyPair generateKeyPair(String algorithm) {
        int keySize = KEY_SIZE;
        if ("ECIES".equalsIgnoreCase(algorithm)) {
            // ECIES算法对KEY的长度有要求，此处默认256
            keySize = 256;
        }
        return generateKeyPair(algorithm, keySize);
    }

    /**
     * 生成 {@link KeyPair}
     * 仅用于非对称加密密钥对生成
     *
     * @param algorithm
     * @param keySize
     * @return
     */
    public KeyPair generateKeyPair(String algorithm, int keySize) {
        // SM2算法需要单独定义其曲线生成
        if ("SM2".equalsIgnoreCase(algorithm)) {
            final ECGenParameterSpec sm2p256v1 = new ECGenParameterSpec(SM2_DEFAULT_CURVE);
            return generateKeyPair(algorithm, keySize, null, sm2p256v1);
        }

        return generateKeyPair(algorithm, keySize, null, (AlgorithmParameterSpec[]) null);
    }

    /**
     * 生成 {@link KeyPair}
     * 仅用于非对称加密密钥对生成
     *
     * @param algorithm
     * @param params
     * @return
     */
    public KeyPair generateKeyPair(String algorithm, AlgorithmParameterSpec params) {
        return generateKeyPair(algorithm, KEY_SIZE, null, params);
    }

    /**
     * 生成 {@link KeyPair}
     * 仅用于非对称加密密钥对生成
     * <p>
     * 对于非对称加密算法，密钥长度有严格限制，具体如下：
     *
     * <p>
     * <b>RSA：</b>
     * <pre>
     * RS256、PS256：2048 bits
     * RS384、PS384：3072 bits
     * RS512、RS512：4096 bits
     * </pre>
     *
     * <p>
     * <b>EC（Elliptic Curve）：</b>
     * <pre>
     * EC256：256 bits
     * EC384：384 bits
     * EC512：512 bits
     * </pre>
     *
     * @param algorithm
     * @param keySize
     * @param random
     * @param params
     * @return
     */
    @SneakyThrows
    public KeyPair generateKeyPair(String algorithm, int keySize, SecureRandom random, AlgorithmParameterSpec... params) {
        algorithm = getAlgorithmAfterWith(algorithm);
        final KeyPairGenerator keyPairGen = getKeyPairGenerator(algorithm);
        initModulus(keyPairGen, algorithm, keySize, random);
        initParams(keyPairGen, random, params);
        return keyPairGen.generateKeyPair();
    }

    /**
     * 从 {@link KeyStore} 获取 {@link KeyPair}
     *
     * @param keyStore
     * @param alias
     * @param password
     * @return
     */
    @SneakyThrows
    public static KeyPair getKeyPair(KeyStore keyStore, String alias, char[] password) {
        PublicKey publicKey = getPublicKey(keyStore, alias);
        PrivateKey privateKey = getPrivateKey(keyStore, alias, password);
        return new KeyPair(publicKey, privateKey);
    }

    /* ********************************************** KeyStore ********************************************** */

    /**
     * 从文件中读取 JKS KeyStore
     *
     * @param keyFile
     * @param password
     * @return
     */
    public static KeyStore readJKSKeyStore(File keyFile, char[] password) {
        return readKeyStore(KEY_TYPE_JKS, keyFile, password);
    }

    /**
     * 从流中读取 JKS KeyStore
     *
     * @param in
     * @param password
     * @return
     */
    public static KeyStore readJKSKeyStore(InputStream in, char[] password) {
        return readKeyStore(KEY_TYPE_JKS, in, password);
    }

    /**
     * 从文件中读取 PKCS12 KeyStore
     *
     * @param keyFile
     * @param password
     * @return
     */
    public static KeyStore readPKCS12KeyStore(File keyFile, char[] password) {
        return readKeyStore(KEY_TYPE_PKCS12, keyFile, password);
    }

    /**
     * 从流中读取 PKCS12 KeyStore
     *
     * @param in
     * @param password
     * @return
     */
    public static KeyStore readPKCS12KeyStore(InputStream in, char[] password) {
        return readKeyStore(KEY_TYPE_PKCS12, in, password);
    }

    /**
     * 从文件中读取指定类型的 KeyStore
     *
     * @param type
     * @param keyFile
     * @param password
     * @return
     */
    @SneakyThrows
    public static KeyStore readKeyStore(String type, File keyFile, char[] password) {
        try (InputStream in = Files.newInputStream(keyFile.toPath())) {
            return readKeyStore(type, in, password);
        }
    }

    /**
     * 从流中读取指定类型的 KeyStore
     *
     * @param type
     * @param in
     * @param password
     * @return
     */
    @SneakyThrows
    public static KeyStore readKeyStore(String type, InputStream in, char[] password) {
        KeyStore keyStore = KeyStore.getInstance(type);
        keyStore.load(in, password);
        return keyStore;
    }

    /* ********************************************** Certificate ********************************************** */

    /**
     * 从流中读取X509证书
     *
     * @param in
     * @return
     */
    public static Certificate readX509Certificate(InputStream in) {
        return readCertificate(CERT_TYPE_X509, in);
    }

    /**
     * 从流中读取指定类型的证书
     *
     * @param type
     * @param in
     * @return
     */
    @SneakyThrows
    public static Certificate readCertificate(String type, InputStream in) {
        return getCertificateFactory(type).generateCertificate(in);
    }

    /**
     * 从流中读取指定类型的证书
     *
     * @param type
     * @param in
     * @param alias
     * @param password
     * @return
     */
    @SneakyThrows
    public static Certificate readCertificate(String type, InputStream in, String alias, char[] password) {
        final KeyStore keyStore = readKeyStore(type, in, password);
        return keyStore.getCertificate(alias);
    }

    /* ********************************************** Other ********************************************** */

    /**
     * 获取 {@link KeyGenerator}
     *
     * @param algorithm 对称加密算法
     * @return
     */
    @SneakyThrows
    public static KeyGenerator getKeyGenerator(String algorithm) {
        return KeyGenerator.getInstance(getMainAlgorithm(algorithm));
    }

    /**
     * 获取 {@link KeyPairGenerator}
     *
     * @param algorithm 非对称加密算法
     * @return
     */
    @SneakyThrows
    public static KeyPairGenerator getKeyPairGenerator(String algorithm) {
        return KeyPairGenerator.getInstance(getMainAlgorithm(algorithm));
    }

    /**
     * 获取 {@link SecretKeyFactory}
     *
     * @param algorithm 对称加密算法
     * @return
     */
    @SneakyThrows
    public SecretKeyFactory getSecretKeyFactory(String algorithm) {
        return SecretKeyFactory.getInstance(getMainAlgorithm(algorithm));
    }

    /**
     * 获取 {@link KeyFactory}
     *
     * @param algorithm 非对称加密算法
     * @return
     */
    @SneakyThrows
    public KeyFactory getKeyFactory(String algorithm) {
        return KeyFactory.getInstance(getMainAlgorithm(algorithm));
    }

    /**
     * 获取 {@link CertificateFactory}
     *
     * @param type 证书类型
     * @return
     */
    @SneakyThrows
    public CertificateFactory getCertificateFactory(String type) {
        return CertificateFactory.getInstance(type);
    }

    /**
     * 获取主体算法名
     * 例如RSA/ECB/PKCS1Padding的主体算法是RSA
     *
     * @param algorithm /算法/分块模式/填充模式
     * @return
     */
    public String getMainAlgorithm(String algorithm) {
        final int slashIndex = algorithm.indexOf('/');
        if (slashIndex > 0) {
            return algorithm.substring(0, slashIndex);
        }
        return algorithm;
    }

    /**
     * 获取用于密钥生成的算法
     * 获取XXXwithXXX算法的后半部分算法，如果为ECDSA或SM2，返回算法为EC
     *
     * @param algorithm XXXwithXXX
     * @return
     */
    public String getAlgorithmAfterWith(String algorithm) {
        algorithm = algorithm.toUpperCase();
        if (algorithm.startsWith("ECIESWITH")) {
            return "EC";
        }

        int indexOfWith = algorithm.lastIndexOf("WITH");
        if (indexOfWith > 0) {
            algorithm = algorithm.substring(indexOfWith + "WITH".length());
        }
        if ("ECDSA".equals(algorithm) || "SM2".equals(algorithm) || "ECIES".equals(algorithm)) {
            algorithm = "EC";
        }
        return algorithm;
    }

    /**
     * 获取 {@link KeySpec}
     *
     * @param algorithm
     * @param key
     * @return
     */
    @SneakyThrows
    private KeySpec getKeySpec(String algorithm, byte[] key) {
        algorithm = algorithm.toUpperCase();
        byte[] bytes = Arrays.copyOf(key, KEY_BITS);
        Arrays.fill(bytes, key.length, bytes.length, (byte) 48);
        char[] chars = new String(bytes, StandardCharsets.UTF_8).toCharArray();

        // PBE密钥规范
        if (algorithm.startsWith("PBE")) {
            return new PBEKeySpec(chars, bytes, ITERATION_COUNT, KEY_SIZE);
        }
        if (algorithm.startsWith("DES")) {
            // DES密钥规范
            if (algorithm.startsWith("DESEDE")) {
                // DESede兼容
                // bytes.length不小于24
                return new DESedeKeySpec(bytes);
            }
            // bytes.length不小于8
            return new DESKeySpec(bytes);
        }

        return null;
    }

    /**
     * 初始化密钥模（modulus）
     *
     * @param keyPairGen
     * @param algorithm
     * @param keySize
     * @param random
     */
    private void initModulus(KeyPairGenerator keyPairGen, String algorithm, int keySize, SecureRandom random) {
        if (keySize > 0) {
            // key长度适配修正
            if ("EC".equalsIgnoreCase(algorithm) && keySize > 256) {
                // 对于EC（EllipticCurve）算法，密钥长度有限制，在此使用默认256
                keySize = 256;
            }
            if (Objects.isNull(random)) {
                keyPairGen.initialize(keySize);
            } else {
                keyPairGen.initialize(keySize, random);
            }
        }
    }

    /**
     * 初始化算法参数规范
     *
     * @param keyPairGen
     * @param random
     * @param params
     */
    @SneakyThrows
    private void initParams(KeyPairGenerator keyPairGen, SecureRandom random, AlgorithmParameterSpec... params) {
        if (Objects.nonNull(params)) {
            for (AlgorithmParameterSpec param : params) {
                if (Objects.isNull(param)) {
                    continue;
                }
                if (Objects.isNull(random)) {
                    keyPairGen.initialize(param);
                } else {
                    keyPairGen.initialize(param, random);
                }
            }
        }
    }
}
