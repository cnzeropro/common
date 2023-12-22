package org.zero.common.core.util.java.codec.random;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 随机数生成器类型
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/6/20
 */
@AllArgsConstructor
@Getter
public enum RandomType {
    /**
     * java.util.Random
     */
    RANDOM("Random"),
    /**
     * java.util.concurrent.ThreadLocalRandom
     */
    THREAD_LOCAL_RANDOM("ThreadLocalRandom"),
    /**
     * java.security.SecureRandom
     */
    SECURE_RANDOM("SecureRandom"),
    ;

    private final String type;
}
