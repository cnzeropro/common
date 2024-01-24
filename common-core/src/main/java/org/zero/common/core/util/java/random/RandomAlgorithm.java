package org.zero.common.core.util.java.random;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SecureRandom 底层算法
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/6/20
 */
@AllArgsConstructor
@Getter
public enum RandomAlgorithm {
    /**
     * 从本机操作系统底层获取随机数
     */
    NATIVE_PRNG("NativePRNG"),
    /**
     * 从本机操作系统底层获取随机数，必要时进行阻止。例如，类UNIX系统上的/dev/random
     */
    NATIVE_PRNG_BLOCKING("NativePRNGBlocking"),
    /**
     * 从本机操作系统底层获取随机数，无需阻塞以防止应用程序过度暂停。例如，类UNIX系统上的/dev/urandom
     */
    NATIVE_PRNG_NONBLOCKING("NativePRNGNonBlocking"),
    /**
     * 从底层安装和配置的PKCS11库中获取随机数
     */
    PKCS11("PKCS11"),
    /**
     * SUN程序提供的伪随机数生成（PRNG）算法
     */
    SHA1PRNG("SHA1PRNG"),
    /**
     * 从Windows操作系统底层获取随机数
     */
    WINDOWS_PRNG("Windows-PRNG"),
    ;

    private final String algorithm;
}
