package org.zero.common.core.util.java.codec;

import cn.hutool.core.util.HexUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息摘要算法工具类
 *
 * @author Zero (cnzeropro@qq.com)
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest">MessageDigest for JDK 8</a>
 * @since 2022/6/11
 */
@UtilityClass
public class MessageDigestUtil {
    /**
     * <li>MD2
     * <li>MD5
     * <li>SHA-1
     * <li>SHA-224
     * <li>SHA-256
     * <li>SHA-384
     * <li>SHA-512
     * <li>SHA-512/224
     * <li>SHA-512/256
     */
    public final String DEFAULT_ALGORITHM = "SHA-512/256";

    /**
     * 获取消息摘要
     *
     * @param content 原始信息
     * @return 摘要信息
     */
    public String digest(String content) {
        return digest(DEFAULT_ALGORITHM, content, StandardCharsets.UTF_8);
    }

    /**
     * 获取消息摘要
     *
     * @param content 原始信息
     * @param charset 字符集
     * @return 摘要信息
     */
    public String digest(String content, Charset charset) {
        return digest(DEFAULT_ALGORITHM, content, charset);
    }

    /**
     * 获取消息摘要
     *
     * @param algorithm 摘要算法
     * @param content   原始信息
     * @return 摘要信息
     */
    public String digest(String algorithm, String content) {
        return digest(algorithm, content, StandardCharsets.UTF_8);
    }

    /**
     * 获取消息摘要
     *
     * @param algorithm 摘要算法
     * @param content   原始信息
     * @param charset   字符集
     * @return 摘要信息
     */
    @SneakyThrows
    public String digest(String algorithm, String content, Charset charset) {
        // 获取摘要算法实例
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        // 获取摘要结果
        byte[] bytes = messageDigest.digest(content.getBytes(charset));
        // byte数组转16进制字符串
        return HexUtil.encodeHexStr(bytes);
    }

    public List<Provider.Service> listAlgorithms() {
        return Arrays.stream(Security.getProviders())
                .flatMap(p -> p.getServices().stream())
                .filter(s -> "MessageDigest".equals(s.getType())).collect(Collectors.toList());
    }
}
