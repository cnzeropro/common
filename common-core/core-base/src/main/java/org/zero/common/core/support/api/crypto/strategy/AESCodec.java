package org.zero.common.core.support.api.crypto.strategy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.symmetric.AES;
import lombok.Getter;
import lombok.Setter;
import org.zero.common.core.support.api.crypto.CodecUtil;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * AES（Advanced Encryption Standard，又称 Rijndael）加解密
 * <p>
 * 算法建议：AES/CBC/PKCS5Padding<br>
 * 密钥长度推荐：192 bit。长度越大，性能越差（加解密速度慢），但安全性越高，反之则反。<br>
 * 密钥只支持三种密钥长度：
 * <table>
 *     <tr>
 *         <th>比特（bit）</th>
 *         <th>字节（byte）</th>
 *     </tr>
 *     <tr>
 *         <td>128</td>
 *         <td>16</td>
 *     </tr>
 *     <tr>
 *         <td>192</td>
 *         <td>24</td>
 *     </tr>
 *     <tr>
 *         <td>256</td>
 *         <td>32</td>
 *     </tr>
 * </table>
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/24
 */
@Setter
@Getter
public class AESCodec extends BaseCodecStrategy {
    public static final String DEFAULT_ALGORITHM = "AES/ECB/PKCS5Padding";
    public static final String DEFAULT_KEY = "iMewXIZGhkGQZtMkOLVLug==";

    private final AES aes;

    @Override
    public byte[] encrypt(byte[] source) {
        return aes.encrypt(source);
    }

    @Override
    public byte[] decrypt(byte[] source) {
        return aes.decrypt(source);
    }

    public static AESCodec of(StrategyContext context) {
        return new AESCodec(context);
    }

    protected AESCodec(StrategyContext context) {
        super(context);
        String algorithm = context.getAlgorithm();
        Properties properties = context.getProperties();
        if (!CharSequenceUtil.contains(algorithm, CodecUtil.DEFAULT_ALGORITHM_SEPARATOR)) {
            algorithm = DEFAULT_ALGORITHM;
        }
        List<String> algorithmInfos = CharSequenceUtil.split(algorithm, CodecUtil.DEFAULT_ALGORITHM_SEPARATOR);
        String mode = CollUtil.get(algorithmInfos, 1);
        String padding = CollUtil.get(algorithmInfos, 2);
        byte[] iv = Optional.ofNullable(properties.getProperty("iv"))
                .map(CodecUtil::decode)
                .orElse(null);
        this.aes = new AES(mode, padding, context.getKey(), iv);
    }
}
