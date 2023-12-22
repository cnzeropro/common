package org.zero.common.core.util.jasypt;

import lombok.experimental.UtilityClass;
import org.jasypt.encryption.StringEncryptor;
import org.zero.common.core.util.spring.context.SpringContextUtils;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/12/8
 */
@UtilityClass
public class JasyptUtils {
    /* **************************************************** Public **************************************************** */

    /**
     * 加密
     */
    public String encrypt(String src) {
        return getEncryptor().encrypt(src);
    }

    /**
     * 解密
     */
    public String decrypt(String src) {
        return getEncryptor().decrypt(src);
    }

    /* **************************************************** Private **************************************************** */
    private StringEncryptor encryptor;

    private StringEncryptor getEncryptor() {
        if (Objects.isNull(encryptor)) {
            synchronized (JasyptUtils.class) {
                if (Objects.isNull(encryptor)) {
                    encryptor = SpringContextUtils.getBean(StringEncryptor.class);
                }
            }
        }
        return encryptor;
    }
}
