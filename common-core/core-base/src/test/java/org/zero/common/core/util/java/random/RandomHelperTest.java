package org.zero.common.core.util.java.random;

import org.junit.jupiter.api.Test;
import org.zero.common.core.util.java.util.RandomHelper;
import org.zero.common.core.util.java.util.RandomUtil;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/11/29
 */
class RandomHelperTest {
    RandomHelper randomHelper = new RandomHelper(RandomUtil.getRandom());

    @Test
    void nextChar() {
        System.out.println(randomHelper.nextChar());
    }

    @Test
    void nextString() {
        System.out.println(randomHelper.nextString(50));
    }

    @Test
    void nextChinese() {
        System.out.println(randomHelper.nextChinese());
    }

    @Test
    void nextChineseString() {
        System.out.println(randomHelper.nextChineseString(50));
    }
}