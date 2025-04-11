package org.zero.common.core.support.captcha;

public class ArithmeticCaptchaCreator extends BaseTextCaptchaCreator<ArithmeticCaptchaCreator> {
    /**
     * 默认生成运算用的随机数边界
     */
    protected static final int NUM_BOUND = 50;

    public static ArithmeticCaptchaCreator create() {
        return new ArithmeticCaptchaCreator();
    }

    @Override
    public ArithmeticCaptchaCreator createTexts() {
        return this.createTexts(NUM_BOUND);
    }


    public ArithmeticCaptchaCreator createTexts(int bound) {
        // 生成随机算术式
        int num1 = random.nextInt(bound) + 1;
        int num2 = random.nextInt(bound) + 1;
        int result;
        String[] texts = new String[4];
        texts[0] = String.valueOf(num1);
        texts[2] = String.valueOf(num2);
        texts[3] = " = ";
        if (num1 <= num2) {
            texts[1] = "+";
            result = num1 + num2;
        } else {
            texts[1] = "-";
            result = num1 - num2;
        }
        this.texts = texts;
        this.code = String.valueOf(result);
        return this;
    }
}
