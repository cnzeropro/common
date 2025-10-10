package org.zero.common.core.util.java.util;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static org.zero.common.core.util.java.util.RandomUtil.BASE_CHAR_LETTER_NUMBER_WITH_UPPER;
import static org.zero.common.core.util.java.util.RandomUtil.BASE_LETTER_NUMBER_WITH_UPPER;
import static org.zero.common.core.util.java.util.RandomUtil.obtainRandom;
import static org.zero.common.core.util.java.util.RandomUtil.obtainSecureRandom;
import static org.zero.common.core.util.java.util.RandomUtil.obtainStrongRandom;
import static org.zero.common.core.util.java.util.RandomUtil.obtainThreadLocalRandom;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/6/20
 */
public class RandomHelper {
    /* ********************************************************** 静态构造方法 ********************************************************** */
    public static RandomHelper create() {
        return create(obtainRandom());
    }

    public static RandomHelper create(Random random) {
        return new RandomHelper(Objects.isNull(random) ? obtainRandom() : random);
    }

    public static RandomHelper create(RandomType randomType) {
        return create(obtainRandom(randomType));
    }

    public static RandomHelper createWithThreadLocalRandom() {
        return create(obtainThreadLocalRandom());
    }

    public static RandomHelper createWithSecureRandom() {
        return create(obtainSecureRandom());
    }

    public static RandomHelper createWithStrongRandom() {
        return create(obtainStrongRandom());
    }

    public static RandomHelper createWithSecureRandom(RandomAlgorithm algorithm) {
        return create(obtainSecureRandom(algorithm));
    }

    /* ********************************************************** 实例 ********************************************************** */

    /**
     * 持有的 Random 实例
     */
    @Getter
    @Setter
    private Random random;

    /**
     * 私有化构造器
     */
    private RandomHelper(Random random) {
        this.random = random;
    }

    /**
     * 获取随机字符
     */
    public char randomChar() {
        return randomChar(BASE_CHAR_LETTER_NUMBER_WITH_UPPER);
    }

    /**
     * 从指定字符串获取随机字符
     */
    public char randomChar(String baseStr) {
        return baseStr.charAt(randomInt(baseStr.length()));
    }

    /**
     * 获取随机字符串
     */
    public String randomString(int size) {
        return randomString(size, BASE_LETTER_NUMBER_WITH_UPPER);
    }

    /**
     * 从指定字符串获取指定长度的随机字符串
     */
    public String randomString(int size, String baseStr) {
        return random.ints(size, 0, baseStr.length())
                .mapToObj(i -> String.valueOf(baseStr.charAt(i)))
                .collect(Collectors.joining());
    }

    /**
     * 获取随机中文字符
     */
    public char randomChinese() {
        return (char) randomInt('\u4E00', '\u9FFF');
    }

    /**
     * 获取指定长度的随机中文字符串
     */
    public String randomChineseString(int size) {
        return random.ints(size, '\u4E00', '\u9FFF')
                .mapToObj(i -> String.valueOf((char) i))
                .collect(Collectors.joining());
    }

    /**
     * 获取0到指定范围的随机整数（包含0不包含bound）
     */
    public int randomInt(int bound) {
        return random.nextInt(bound);
    }

    /**
     * 获取指定范围的随机整数（包含下界不包含上界）
     */
    public int randomInt(int min, int max) {
        return randomInt(max - min) + min;
    }
}
