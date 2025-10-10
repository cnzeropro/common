package org.zero.common.core.util.java.util;

import lombok.SneakyThrows;
import org.zero.common.data.exception.UtilException;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/28
 */
public class RandomUtil {
    /**
     * 用于随机选的数字
     */
    public static final String BASE_NUMBER = "0123456789";
    /**
     * 用于随机选的字母
     */
    public static final String BASE_LETTER = "abcdefghijklmnopqrstuvwxyz";

    /**
     * 用于随机选的字符
     */
    public static final String BASE_CHAR = "~!@#$%^&*_-+=|";

    /**
     * 用于随机选的字母和数字，默认只有字母小写
     */
    public static final String BASE_LETTER_NUMBER = BASE_LETTER + BASE_NUMBER;

    /**
     * 用于随机选的字母和数字，包含字母大写
     */
    public static final String BASE_LETTER_NUMBER_WITH_UPPER = BASE_LETTER.toUpperCase() + BASE_LETTER_NUMBER;

    /**
     * 用于随机选的字符、字母和数字，默认只有字母小写
     */
    public static final String BASE_CHAR_LETTER_NUMBER = BASE_LETTER_NUMBER + BASE_CHAR;

    /**
     * 用于随机选的字符、字母和数字，包含字母大写
     */
    public static final String BASE_CHAR_LETTER_NUMBER_WITH_UPPER = BASE_LETTER_NUMBER_WITH_UPPER + BASE_CHAR;

    /**
     * 根据 RandomType 获取 Random 实例
     */
    public static Random obtainRandom(RandomType randomType) {
        switch (randomType) {
            case RANDOM:
                return obtainRandom();
            case THREAD_LOCAL_RANDOM:
                return obtainThreadLocalRandom();
            case SECURE_RANDOM:
                return obtainSecureRandom();
            default:
                throw new UtilException("Unknown random type");
        }
    }

    /**
     * 获取 Random 实例
     */
    public static Random obtainRandom() {
        return new Random();
    }

    /**
     * 获取 ThreadLocalRandom 实例
     */
    public static ThreadLocalRandom obtainThreadLocalRandom() {
        return ThreadLocalRandom.current();
    }

    /**
     * 获取 SecureRandom 实例
     */
    public static SecureRandom obtainSecureRandom() {
        return new SecureRandom();
    }

    /**
     * 获取强 SecureRandom 实例
     */
    @SneakyThrows
    public static SecureRandom obtainStrongRandom() {
        return SecureRandom.getInstanceStrong();
    }

    /**
     * 根据随机算法获取 SecureRandom 实例
     */
    @SneakyThrows
    public static SecureRandom obtainSecureRandom(RandomAlgorithm algorithm) {
        return SecureRandom.getInstance(algorithm.getAlgorithm());
    }
}
