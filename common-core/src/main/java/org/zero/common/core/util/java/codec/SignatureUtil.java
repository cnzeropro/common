package org.zero.common.core.util.java.codec;

import cn.hutool.core.util.HexUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 签名算法工具类
 *
 * @author Zero (cnzeropro@qq.com)
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Signature">Signature for JDK 8</a>
 * @since 2022/6/11
 */
@UtilityClass
public class SignatureUtil {
    /**
     * [digest]with[encryption]
     * <li>NONEwithRSA
     * <li>MD2withRSA
     * <li>MD5withRSA
     * <li>SHA1withRSA
     * <li>SHA224withRSA
     * <li>SHA256withRSA
     * <li>SHA384withRSA
     * <li>SHA512withRSA
     * <li>SHA512/224withRSA
     * <li>SHA512/256withRSA
     * <li>RSASSA-PSS
     * <li>NONEwithDSA
     * <li>SHA1withDSA
     * <li>SHA224withDSA
     * <li>SHA256withDSA
     * <li>SHA384withDSA
     * <li>SHA512withDSA
     * <li>NONEwithECDSA
     * <li>SHA1withECDSA
     * <li>SHA224withECDSA
     * <li>SHA256withECDSA
     * <li>SHA384withECDSA
     * <li>SHA512withECDSA
     */
    public final String DEFAULT_ALGORITHM = "SHA512/256withRSA";

    /**
     * 签名
     */
    public String sign(String content, PrivateKey key) {
        return sign(DEFAULT_ALGORITHM, content, StandardCharsets.UTF_8, key, null);
    }

    /**
     * 签名
     */
    public String sign(String algorithm, String content, PrivateKey key) {
        return sign(algorithm, content, StandardCharsets.UTF_8, key, null);
    }

    /**
     * 签名
     */
    public String sign(String algorithm, String content, Charset charset, PrivateKey key) {
        return sign(algorithm, content, charset, key, null);
    }

    /**
     * 签名
     */
    @SneakyThrows
    public String sign(String algorithm, String content, Charset charset, PrivateKey key, SecureRandom random) {
        // 获取签名算法实例
        Signature signature = Signature.getInstance(algorithm);
        // 初始化签名对象
        if (Objects.isNull(random)) {
            signature.initSign(key);
        } else {
            signature.initSign(key, random);
        }
        // 更新签名
        signature.update(content.getBytes(charset));
        // 获取签名信息
        byte[] bytes = signature.sign();
        // byte数组转16进制字符串
        return HexUtil.encodeHexStr(bytes);
    }

    /**
     * 验签
     */
    public boolean verify(String sign, String content, PublicKey key) {
        return verify(DEFAULT_ALGORITHM, sign, content, StandardCharsets.UTF_8, key);
    }

    /**
     * 验签
     */
    public boolean verify(String algorithm, String sign, String content, PublicKey key) {
        return verify(algorithm, sign, content, StandardCharsets.UTF_8, key);
    }

    /**
     * 验签
     */
    @SneakyThrows
    public boolean verify(String algorithm, String sign, String content, Charset charset, PublicKey key) {
        // 获取签名算法实例
        Signature signature = Signature.getInstance(algorithm);
        // 初始化签名对象
        signature.initVerify(key);
        // 更新签名
        signature.update(content.getBytes(charset));
        // 验签
        return signature.verify(HexUtil.decodeHex(sign));
    }

    public List<Provider.Service> listAlgorithms() {
        return Arrays.stream(Security.getProviders())
                .flatMap(p -> p.getServices().stream())
                .filter(s -> "Signature".equals(s.getType()))
                .collect(Collectors.toList());
    }
}
