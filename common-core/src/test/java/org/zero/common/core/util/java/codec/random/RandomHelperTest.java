package org.zero.common.core.util.java.codec.random;

import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/11/29
 */
class RandomHelperTest {
    RandomHelper randomHelper = RandomHelper.createWithStrongRandom();

    @Test
    void randomChar() {
        System.out.println(randomHelper.randomChar());
    }

    @Test
    void randomString() {
        System.out.println(randomHelper.randomString(50));
    }

    @Test
    void randomChinese() {
        System.out.println(randomHelper.randomChinese());
    }

    @Test
    void randomChineseString() {
        System.out.println(randomHelper.randomChineseString(50));
    }

    @Test
    void randomInt() {
        System.out.println(randomHelper.randomInt(0, 100));
    }
}