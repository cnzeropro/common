package org.zero.common.core.util.java.captcha;

/**
 * @author zero
 * @since 2023/12/19
 */
public enum CaptchaImageType {
    PNG,
    JPEG,
    JPG,
    GIF,
    ;

    /**
     * 通过图片类型（字符串）获取枚举值，没有匹配到时，默认返回PNG
     */
    public static CaptchaImageType getImageType(String type) {
        for (CaptchaImageType imageType : values()) {
            if (imageType.name().equalsIgnoreCase(type)) {
                return imageType;
            }
        }
        return PNG;
    }
}
