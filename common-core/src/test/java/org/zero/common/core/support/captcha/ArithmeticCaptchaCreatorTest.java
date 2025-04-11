package org.zero.common.core.support.captcha;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/9
 */
class ArithmeticCaptchaCreatorTest {

    @Test
    void creator() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String result = ArithmeticCaptchaCreator.create()
                .createTexts()
                .createImg()
                .createBase64()
                .outAndGet(outputStream);
        System.out.println(result);
    }
}