package org.zero.common.core.util.java.captcha;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author yufa.wang (yufa.wang@ronganchina.com)
 * @since 2023/12/19
 */
@Slf4j
@Setter
@Getter
@Accessors(chain = true)
public abstract class BaseCaptchaCreator<T extends BaseCaptchaCreator<T>> {
    /**
     * 默认图片大小
     */
    protected static final int IMG_WIDTH = 150;
    protected static final int IMG_HEIGHT = 30;
    /**
     * 默认干扰线数量
     */
    protected static final int LINE_COUNT = 15;
    /**
     * 默认麻点数量
     */
    protected static final int POINT_COUNT = 150;
    /**
     * 默认图片格式
     */
    protected static final CaptchaImageType IMG_TYPE = CaptchaImageType.PNG;

    /**
     * base64编码器
     */
    protected static final Base64.Encoder base64Encoder = Base64.getEncoder();
    /**
     * base64图片前缀
     */
    protected static final String BASE64_PREFIX = "data:image/%s;base64,";
    /**
     * 随机数生成器，默认采用强随机数生成器，除非该 jvm 没有该 SecureRandom 实现（基本不可能）
     */
    protected static final Random random;

    static {
        Random tempRandom;
        try {
            tempRandom = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            log.warn("No Strong SecureRandom implementation", e);
            tempRandom = ThreadLocalRandom.current();
        }
        random = tempRandom;
    }

    /**
     * 验证码
     */
    protected String code;
    /**
     * BufferedImage
     */
    protected BufferedImage bufferedImage;
    /**
     * 图片Base64字符串
     */
    protected String imgBase64;

    public T createImg() {
        return this.createImg(IMG_WIDTH, IMG_HEIGHT);
    }

    public T createImg(int width, int height) {
        return this.createImg(width, height, LINE_COUNT, POINT_COUNT);
    }

    public T createImg(int width, int height, int line, int point) {
        throw new IllegalStateException("Method not implemented");
    }

    public T createBase64() {
        return this.createBase64(IMG_TYPE);
    }

    public T createBase64(CaptchaImageType imgType) {
        String type = imgType.name();
        return this.createBase64(type);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public T createBase64(String imgType) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        this.out(imgType, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        outputStream.close();
        String base64Content = base64Encoder.encodeToString(byteArray);
        String base64Prefix = String.format(BASE64_PREFIX, imgType.toLowerCase());
        this.imgBase64 = base64Prefix + base64Content;
        return (T) this;
    }

    public void out(OutputStream outputStream) {
        this.out(IMG_TYPE, outputStream);
    }

    public void out(CaptchaImageType imageType, OutputStream outputStream) {
        this.out(imageType.name(), outputStream);
    }

    @SneakyThrows
    public void out(String imageType, OutputStream outputStream) {
        ImageIO.write(bufferedImage, imageType, outputStream);
    }

    public String outAndGet(OutputStream outputStream) {
        this.out(IMG_TYPE, outputStream);
        return this.code;
    }

    public String outAndGet(String imageType, OutputStream outputStream) {
        this.out(imageType, outputStream);
        return this.code;
    }

    public String outAndGet(CaptchaImageType imageType, OutputStream outputStream) {
        this.out(imageType, outputStream);
        return this.code;
    }

    protected static Color getRandomColor(int r, int g, int b) {
        return new Color(random.nextInt(r), random.nextInt(g), random.nextInt(b));
    }
}
