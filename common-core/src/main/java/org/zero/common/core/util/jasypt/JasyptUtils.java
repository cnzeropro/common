package org.zero.common.core.util.jasypt;

import lombok.Setter;
import org.jasypt.encryption.StringEncryptor;
import org.zero.common.core.util.spring.SpringUtils;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/12/8
 */
// @UtilityClass
public class JasyptUtils {
    /* **************************************************** Delegate **************************************************** */
    /**
     * 加密
     */
    public static String encrypt(String src) {
        return getEncryptor().encrypt(src);
    }

    /**
     * 解密
     */
    public static String decrypt(String src) {
        return getEncryptor().decrypt(src);
    }

    /* **************************************************** Init **************************************************** */
    @Setter
    private static volatile StringEncryptor encryptor;

    /**
     * 获取工具类中 {@link StringEncryptor} 实例
     * <p>
     * 注意：请勿修改获取到的 {@link StringEncryptor} 实例中的属性或者使其为 null，否则可能会影响工具类的使用。
     */
    public static StringEncryptor getEncryptor() {
        checkAndCreate();
        return encryptor;
    }

    protected static void checkAndCreate() {
        if (Objects.isNull(encryptor)) {
            synchronized (JasyptUtils.class) {
                if (Objects.isNull(encryptor)) {
                    encryptor = SpringUtils.getBean(StringEncryptor.class);
                }
            }
        }
    }

    protected JasyptUtils() {
        throw new UnsupportedOperationException();
    }
}
