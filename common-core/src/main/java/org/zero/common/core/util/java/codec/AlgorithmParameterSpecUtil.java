package org.zero.common.core.util.java.codec;

import cn.hutool.core.util.RandomUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import javax.xml.crypto.dsig.spec.HMACParameterSpec;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECField;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Arrays;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/11/25
 */
@UtilityClass
public class AlgorithmParameterSpecUtil {
    private final String RC2 = "RC2";
    private final String RC5 = "RC5";
    private final String PBE = "PBE";
    private final String AES = "AES";
    private final String DES = "DES";
    private final String RSA = "RSA";
    private final String EC = "EC";
    private final String HMAC = "HMAC";
    private final int KEY_BITS = 128;
    private final int KEY_SIZE = 1024;
    private final int ITERATION_COUNT = 1000;

    /**
     * 使用随机盐值获取算法参数规范
     */
    public AlgorithmParameterSpec getParameterSpec(String algorithm) {
        return getParameterSpec(algorithm, RandomUtil.randomString(KEY_BITS));
    }

    /**
     * 获取算法参数规范
     */
    public AlgorithmParameterSpec getParameterSpec(String algorithm, String salt) {
        return getParameterSpec(algorithm, salt, StandardCharsets.UTF_8);
    }

    /**
     * 获取算法参数规范
     */
    @SneakyThrows
    public AlgorithmParameterSpec getParameterSpec(String algorithm, String salt, Charset charset) {
        algorithm = algorithm.toUpperCase();
        byte[] bytes = Arrays.copyOf(salt.getBytes(charset), KEY_BITS);

        if (algorithm.startsWith(RC2)) {
            // bytes.length不小于8
            return new RC2ParameterSpec(KEY_SIZE, bytes);
        }
        if (algorithm.startsWith(RC5)) {
            // bytes.length不小于KEY_SIZE/4
            return new RC5ParameterSpec(1, 4, KEY_SIZE, bytes);
        }
        if (algorithm.startsWith(PBE)) {
            if (algorithm.contains(AES)) {
                return new PBEParameterSpec(Arrays.copyOf(bytes, 8), ITERATION_COUNT, new IvParameterSpec(Arrays.copyOf(bytes, 16)));
            }
            return new PBEParameterSpec(Arrays.copyOf(bytes, 8), ITERATION_COUNT);
        }
        if (algorithm.startsWith(RSA)) {
            return new RSAKeyGenParameterSpec(KEY_SIZE, RSAKeyGenParameterSpec.F4);
        }
        if (algorithm.startsWith(EC)) {
            ECField field = new ECFieldFp(BigInteger.valueOf(ITERATION_COUNT));
            EllipticCurve curve = new EllipticCurve(field, BigInteger.valueOf(KEY_SIZE), BigInteger.valueOf(ITERATION_COUNT), bytes);
            return new ECParameterSpec(curve, ECPoint.POINT_INFINITY, BigInteger.valueOf(KEY_SIZE), ITERATION_COUNT);
        }
        if (algorithm.startsWith(HMAC)) {
            return new HMACParameterSpec(KEY_SIZE);
        }

        return null;
    }
}
