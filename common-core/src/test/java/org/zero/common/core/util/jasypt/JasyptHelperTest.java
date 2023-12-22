package org.zero.common.core.util.jasypt;

import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2021/11/22 15:45
 */
class JasyptHelperTest {
    JasyptHelper jasyptHelper = JasyptHelper.build("abc123");

    @Test
    void encrypt() {
        String encrypt = jasyptHelper.encrypt("zero");
        System.out.println(encrypt);
    }

    @Test
    void decrypt() {
        String decrypt = jasyptHelper.decrypt("D/VYxO2sNAQffZU3PPg45YunKhy04jjTiB8g42TRbJIoBsiM2PL5RE++0cg7zhVc");
        System.out.println(decrypt);
    }
}