package org.zero.common.core.util.java.codec;

import cn.hutool.core.util.HexUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import javax.crypto.Mac;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HMac算法工具类
 *
 * @author Zero (cnzeropro@qq.com)
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Mac">HMac for JDK 8</a>
 * @since 2022/6/11
 */
@UtilityClass
public class HmacUtil {
    /**
     * HMac支持算法：
     * <li>HmacMD5
     * <li>HmacSHA1
     * <li>HmacSHA224
     * <li>HmacSHA256
     * <li>HmacSHA384
     * <li>HmacSHA512
     */
    public final String DEFAULT_HMAC_ALGORITHM = "HmacSHA512";
    /**
     * 密钥支持算法：
     * <li>AES
     * <li>ARCFOUR
     * <li>DES
     * <li>DESede(3DES)
     * <li>PBEWith[digest]And[encryption]: PBEWithMD5AndDES
     * <li>PBEWith[prf]And[encryption]: PBEWithHmacSHA256AndAES_128
     * <li>PBKDF2With[prf]: PBKDF2WithHmacSHA256
     */
    public final String DEFAULT_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * 获取加密摘要信息
     *
     * @param content  原始字串
     * @param password 密钥
     * @return 加密摘要信息
     */
    public String digest(String content, String password) {
        return digest(DEFAULT_HMAC_ALGORITHM, DEFAULT_KEY_ALGORITHM, content, password, StandardCharsets.UTF_8);
    }

    /**
     * 获取加密摘要信息
     *
     * @param content  原始字串
     * @param password 密钥
     * @param charset  字符集
     * @return 加密摘要信息
     */
    public String digest(String content, String password, Charset charset) {
        return digest(DEFAULT_HMAC_ALGORITHM, DEFAULT_KEY_ALGORITHM, content, password, charset);
    }

    /**
     * 获取加密摘要信息
     *
     * @param hmacAlgorithm HMac算法
     * @param content       原始字串
     * @param password      密钥
     * @return 加密摘要信息
     */
    public String digest(String hmacAlgorithm, String content, String password) {
        return digest(hmacAlgorithm, DEFAULT_KEY_ALGORITHM, content, password, StandardCharsets.UTF_8);
    }

    /**
     * 获取加密摘要信息
     *
     * @param hmacAlgorithm HMac算法
     * @param content       原始字串
     * @param password      密钥
     * @param charset       字符集
     * @return 加密摘要信息
     */
    public String digest(String hmacAlgorithm, String content, String password, Charset charset) {
        return digest(hmacAlgorithm, DEFAULT_KEY_ALGORITHM, content, password, charset);
    }

    /**
     * 获取加密摘要信息
     *
     * @param hmacAlgorithm HMac算法
     * @param keyAlgorithm  密钥算法
     * @param content       原始字串
     * @param password      密钥
     * @return 加密摘要信息
     */
    public String digest(String hmacAlgorithm, String keyAlgorithm, String content, String password) {
        return digest(hmacAlgorithm, keyAlgorithm, content, password, StandardCharsets.UTF_8);
    }

    /**
     * 获取加密摘要信息
     *
     * @param hmacAlgorithm HMac算法
     * @param keyAlgorithm  密钥算法
     * @param content       原始字串
     * @param password      密钥
     * @param charset       字符集
     * @return 加密摘要信息
     */
    @SneakyThrows
    public String digest(String hmacAlgorithm, String keyAlgorithm, String content, String password, Charset charset) {
        // 密钥规范
        // Key key = new SecretKeySpec(password.getBytes(charset), keyAlgorithm);
        Key key = KeyUtil.generateKey(keyAlgorithm, password.getBytes(charset));
        // 获取Mac算法实例
        Mac mac = Mac.getInstance(hmacAlgorithm);
        // 初始化
        mac.init(key);
        // 获取摘要结果
        byte[] digest = mac.doFinal(content.getBytes(charset));
        // byte数组转16进制字符串
        return HexUtil.encodeHexStr(digest);
    }

    public List<Provider.Service> listAlgorithms() {
        return Arrays.stream(Security.getProviders())
                .flatMap(p -> p.getServices().stream())
                .filter(s -> "Mac".equals(s.getType())).collect(Collectors.toList());
    }
}
