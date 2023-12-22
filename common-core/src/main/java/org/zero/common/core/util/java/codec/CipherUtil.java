package org.zero.common.core.util.java.codec;

import cn.hutool.core.util.HexUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import javax.crypto.Cipher;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/6/11
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Cipher">Cipher for JDK 8</a>
 */
@UtilityClass
public class CipherUtil {
    /**
     * Cipher支持算法：
     * <li>AES
     * <li>AESWrap
     * <li>ARCFOUR
     * <li>Blowfish
     * <li>DES
     * <li>DESede
     * <li>DESedeWrap
     * <li>ECIES
     * <li>PBEWith[digest]And[encryption]: PBEWithMD5AndDES、PBEWithSHA1AndRC2_40、PBEWithMD5AndTripleDES
     * <li>PBEWith[prf]And[encryption]: PBEWithHmacSHA512AndAES_256
     * <li>RC2
     * <li>RC4
     * <li>RC5
     * <li>RSA
     */
    private final String DEFAULT_ALGORITHM = "PBEWithMD5AndTripleDES";

    /* ******************************************* 加密 ******************************************* */

    /**
     * 加密
     *
     * @param content  明文
     * @param password 密钥
     * @param salt     盐值
     * @return 密文
     */
    public String encrypt(String content, String password, String salt) {
        return encrypt(DEFAULT_ALGORITHM, content, password, salt);
    }

    /**
     * 加密
     *
     * @param algorithm 加密算法
     * @param content   明文
     * @param password  密钥
     * @param salt      盐值
     * @return 密文
     */
    public String encrypt(String algorithm, String content, String password, String salt) {
        return encrypt(algorithm, content, password, salt, StandardCharsets.UTF_8);
    }

    /**
     * 加密
     *
     * @param algorithm 加密算法
     * @param content   明文
     * @param password  密钥
     * @param salt      盐值
     * @param charset   字符集
     * @return 密文
     */
    @SneakyThrows
    public String encrypt(String algorithm, String content, String password, String salt, Charset charset) {
        // 获取密码算法实例
        Cipher cipher = getCipher(algorithm, Cipher.ENCRYPT_MODE, password, salt, charset);
        // 加密
        byte[] bytes = cipher.doFinal(content.getBytes(charset));
        // byte数组转16进制字符串
        return HexUtil.encodeHexStr(bytes);
    }

    /* ******************************************* 解密 ******************************************* */

    /**
     * 解密
     *
     * @param content  密文
     * @param password 密钥
     * @param salt     盐值
     * @return 明文
     */
    public String decrypt(String content, String password, String salt) {
        return decrypt(DEFAULT_ALGORITHM, content, password, salt);
    }

    /**
     * 解密
     *
     * @param algorithm 加密算法
     * @param content   密文
     * @param password  密钥
     * @param salt      盐值
     * @return 明文
     */
    public String decrypt(String algorithm, String content, String password, String salt) {
        return decrypt(algorithm, content, password, salt, StandardCharsets.UTF_8);
    }

    /**
     * 解密
     *
     * @param algorithm 加密算法
     * @param content   密文
     * @param password  密钥
     * @param salt      盐值
     * @param charset   字符集
     * @return 明文
     */
    @SneakyThrows
    public String decrypt(String algorithm, String content, String password, String salt, Charset charset) {
        // 获取密码算法实例
        Cipher cipher = getCipher(algorithm, Cipher.DECRYPT_MODE, password, salt, charset);
        // 解密
        byte[] bytes = cipher.doFinal(HexUtil.decodeHex(content));
        // byte数组转字符串
        return new String(bytes, charset);
    }

    /* ******************************************* 其他 ******************************************* */

    /**
     * 获取当前支持的Cipher算法
     */
    public List<Provider.Service> listAlgorithms() {
        return Arrays.stream(Security.getProviders())
                .flatMap(p -> p.getServices().stream())
                .filter(s -> "Cipher".equals(s.getType())).collect(Collectors.toList());
    }

    /**
     * 获取Cipher实例并初始化
     */
    @SneakyThrows
    public Cipher getCipher(String algorithm, int mode, String password, String salt, Charset charset) {
        // 获取密钥
        Key key = KeyUtil.generateKey(algorithm, password.getBytes(charset));
        // 获取算法参数规格
        AlgorithmParameterSpec param = AlgorithmParameterSpecUtil.getParameterSpec(algorithm, salt, charset);
        return getCipher(algorithm, mode, key, param, SecureRandom.getInstanceStrong());
    }

    /**
     * 获取Cipher实例并初始化
     */
    @SneakyThrows
    public Cipher getCipher(String algorithm, int mode, Key key, AlgorithmParameterSpec param, SecureRandom random) {
        // 获取算法实例
        Cipher cipher = Cipher.getInstance(algorithm);
        // 初始化
        if (Objects.isNull(param)) {
            if (Objects.isNull(random)) {
                cipher.init(mode, key);
            } else {
                cipher.init(mode, key, random);
            }
        } else {
            if (Objects.isNull(random)) {
                cipher.init(mode, key, param);
            } else {
                cipher.init(mode, key, param, random);
            }
        }
        return cipher;
    }
}
