package org.zero.common.core.support.captcha;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.awt.*;
import java.awt.image.BufferedImage;

@Setter
@Getter
@Accessors(chain = true)
public abstract class BaseTextCaptchaCreator<T extends BaseTextCaptchaCreator<T>> extends BaseCaptchaCreator<T> {
    /**
     * 写入图片的文本
     */
    protected String[] texts;

    public  T createTexts(){
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public T createImg() {
        return this.createImg(IMG_WIDTH, IMG_HEIGHT);
    }

    @Override
    public T createImg(int width, int height) {
        return this.createImg(width, height, LINE_COUNT, POINT_COUNT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T createImg(int width, int height, int line, int point) {
        // 生成缓冲图片
        // 创建图像缓冲区
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 通过缓冲区创建一个画布
        Graphics graphics = bufferedImage.getGraphics();
        // 为画布创建背景颜色
        graphics.setColor(Color.white);
        // 填充指定的矩形
        graphics.fillRect(0, 0, width, height);
        // 设置字体
        graphics.setFont(new Font("微软雅黑", Font.BOLD + Font.ITALIC, height / 2));
        // 绘制验证码字符串
        for (int i = 0; i < texts.length; i++) {
            graphics.setColor(getRandomColor(2, 22, 222));
            graphics.drawString(texts[i], (i + 1) * width / 6, height / 4);
        }
        // 生成干扰线
        for (int i = 0; i < line; i++) {
            graphics.setColor(getRandomColor(255, 255, 255));
            graphics.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
        }
        // 随机麻点干扰
        for (int i = 0; i < point; i++) {
            graphics.setColor(getRandomColor(230, 240, 250));
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            // 画点
            graphics.drawLine(x, y, x, y);
        }
        // 关闭资源
        graphics.dispose();
        this.bufferedImage = bufferedImage;
        return (T) this;
    }
}
