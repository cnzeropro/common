package org.zero.common.core.util.java.codec;

import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/11/24
 */
class Base64UtilTest {
    @Test
    void test() {
        String src = "你好abc@123";

        String encode = Base64Util.encode(src);
        System.out.println("encode:" + encode);

        String decode = Base64Util.decode(encode);
        System.out.println("decode:" + decode);
    }
}